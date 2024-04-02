package cn.bootx.platform.daxpay.service.core.payment.allocation.service;

import cn.bootx.platform.common.core.exception.DataNotExistException;
import cn.bootx.platform.daxpay.code.PayChannelEnum;
import cn.bootx.platform.daxpay.service.core.payment.allocation.convert.AllocationGroupConvert;
import cn.bootx.platform.daxpay.service.core.payment.allocation.dao.AllocationGroupManager;
import cn.bootx.platform.daxpay.service.core.payment.allocation.dao.AllocationGroupReceiverManager;
import cn.bootx.platform.daxpay.service.core.payment.allocation.dao.AllocationReceiverManager;
import cn.bootx.platform.daxpay.service.core.payment.allocation.entity.AllocationGroup;
import cn.bootx.platform.daxpay.service.core.payment.allocation.entity.AllocationGroupReceiver;
import cn.bootx.platform.daxpay.service.core.payment.allocation.entity.AllocationReceiver;
import cn.bootx.platform.daxpay.service.param.allocation.AllocationGroupBindParam;
import cn.bootx.platform.daxpay.service.param.allocation.AllocationGroupParam;
import cn.bootx.platform.daxpay.service.param.allocation.AllocationGroupReceiverParam;
import cn.bootx.platform.daxpay.service.param.allocation.AllocationGroupUnbindParam;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分账组服务
 * @author xxm
 * @since 2024/4/1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AllocationGroupService {
    private final AllocationGroupManager groupManager;
    private final AllocationGroupReceiverManager groupReceiverManager;
    private final AllocationReceiverManager receiverManager;

    /**
     * 创建分账组
     */
    public void create(AllocationGroupParam param){
        PayChannelEnum.findByCode(param.getChannel());
        AllocationGroup allocation = AllocationGroupConvert.CONVERT.convert(param);
        groupManager.save(allocation);
    }

    /**
     * 更新分账组
     */
    public void update(AllocationGroupParam param){
        AllocationGroup group = groupManager.findById(param.getId())
                .orElseThrow(() -> new DataNotExistException("未找到分账组"));
        BeanUtil.copyProperties(param,group, CopyOptions.create().ignoreNullValue());
        group.setTotalRate(0);
        groupManager.updateById(group);
    }

    /**
     * 删除分账组
     */
    public void delete(){

    }

    /**
     * 绑定分账接收方
     */
    @Transactional(rollbackFor = Exception.class)
    public void bind(AllocationGroupBindParam param) {
        // 分账组
        AllocationGroup group = groupManager.findById(param.getGroupId())
                .orElseThrow(() -> new DataNotExistException("未找到分账组"));
        // 查询接收方
        List<AllocationGroupReceiverParam> receiverParams = param.getReceivers();
        List<Long> receiverIds =  receiverParams.stream()
                .map(AllocationGroupReceiverParam::getReceiverId)
                .collect(Collectors.toList());
        List<AllocationReceiver> receivers = receiverManager.findAllByIds(receiverIds);
        if (receivers.size() != receiverIds.size()){
            throw new DataNotExistException("传入的分账接收房数量与查询到的不一致");
        }
        // 接收方需要已经同步到三方值系统中
        receivers.stream()
                .filter(receiver -> !receiver.isSync())
                .findAny()
                .ifPresent(receiver -> {
                    throw new DataNotExistException("接收方未同步到三方值系统中");
                });

        // 保存分账接收者
        List<AllocationGroupReceiver> groupReceivers = receivers.stream()
                .map(receiver -> new AllocationGroupReceiver().setGroupId(group.getId())
                        .setReceiverId(receiver.getId())
                        .setRate(receiverParams.get(receivers.indexOf(receiver))
                                .getRate()))
                .collect(Collectors.toList());
        groupReceiverManager.saveAll(groupReceivers);
        // 计算分账比例
        int sum = receiverParams.stream()
                .mapToInt(AllocationGroupReceiverParam::getRate)
                .sum();
        group.setTotalRate(group.getTotalRate() + sum);
        groupManager.updateById(group);
    }

    /**
     * 批量删除分账接收方
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeReceivers(AllocationGroupUnbindParam param){
        // 分账组
        AllocationGroup group = groupManager.findById(param.getGroupId())
                .orElseThrow(() -> new DataNotExistException("未找到分账组"));
        // 删除接收方
        List<AllocationGroupReceiver> receivers = groupReceiverManager.findAllByIds(param.getReceiverIds());
        if (receivers.size() != param.getReceiverIds().size()){
            throw new DataNotExistException("传入的分账接收房数量与查询到的不一致");
        }
        groupReceiverManager.deleteByIds(param.getReceiverIds());
        // 计算分账比例
        int sum = receivers.stream()
                .mapToInt(AllocationGroupReceiver::getRate)
                .sum();
        group.setTotalRate(group.getTotalRate() - sum);
        groupManager.updateById(group);
    }

    /**
     * 删除单个分账接收方
     */
    @Transactional
    public void removeReceiver(Long receiverId){
        AllocationGroupReceiver groupReceiver = groupReceiverManager.findById(receiverId)
                .orElseThrow(() -> new DataNotExistException("未找到分账接收方"));
        AllocationGroup group = groupManager.findById(groupReceiver.getGroupId())
                .orElseThrow(() -> new DataNotExistException("未找到分账组"));
        // 更新分账比例
        group.setTotalRate(group.getTotalRate() - groupReceiver.getRate());
        // 更新接收比例
        groupReceiverManager.updateById(groupReceiver);
        groupManager.deleteById(group);
    }

    /**
     * 修改分账比例
     */
    public void updateRate(Long receiverId, Integer rate){
        AllocationGroupReceiver groupReceiver = groupReceiverManager.findById(receiverId)
                .orElseThrow(() -> new DataNotExistException("未找到分账接收方"));
        AllocationGroup group = groupManager.findById(groupReceiver.getGroupId())
                .orElseThrow(() -> new DataNotExistException("未找到分账组"));
        // 更新分账比例
        group.setTotalRate(group.getTotalRate() - groupReceiver.getRate() + rate);
        // 更新接收比例
        groupReceiver.setRate(rate);
        groupReceiverManager.updateById(groupReceiver);
        groupManager.updateById(group);
    }

}