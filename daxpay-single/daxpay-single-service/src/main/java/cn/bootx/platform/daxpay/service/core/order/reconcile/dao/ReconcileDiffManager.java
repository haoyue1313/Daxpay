package cn.bootx.platform.daxpay.service.core.order.reconcile.dao;

import cn.bootx.platform.common.core.rest.param.PageParam;
import cn.bootx.platform.common.mybatisplus.impl.BaseManager;
import cn.bootx.platform.common.mybatisplus.util.MpUtil;
import cn.bootx.platform.common.query.generator.QueryGenerator;
import cn.bootx.platform.daxpay.service.core.order.reconcile.entity.ReconcileDiff;
import cn.bootx.platform.daxpay.service.param.reconcile.ReconcileDiffQuery;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 *
 * @author xxm
 * @since 2024/2/28
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ReconcileDiffManager extends BaseManager<ReconcileDiffMapper, ReconcileDiff> {

    /**
     * 分页
     */
    public Page<ReconcileDiff> page(PageParam pageParam, ReconcileDiffQuery query){
        Page<ReconcileDiff> mpPage = MpUtil.getMpPage(pageParam, ReconcileDiff.class);
        QueryWrapper<ReconcileDiff> generator = QueryGenerator.generator(query);
        return this.page(mpPage,generator);
    }
}