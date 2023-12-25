package cn.bootx.platform.daxpay.core.payment.common.service;

import cn.bootx.platform.daxpay.code.PaySignTypeEnum;
import cn.bootx.platform.daxpay.common.context.ApiInfoLocal;
import cn.bootx.platform.daxpay.common.context.PlatformLocal;
import cn.bootx.platform.daxpay.common.local.PaymentContextLocal;
import cn.bootx.platform.daxpay.core.payment.pay.service.PayAssistService;
import cn.bootx.platform.daxpay.exception.pay.PayFailureException;
import cn.bootx.platform.daxpay.param.pay.PayCommonParam;
import cn.hutool.core.util.StrUtil;
import com.ijpay.core.kit.PayKit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * 支付签名服务
 * @author xxm
 * @since 2023/12/24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentSignService {

    private static final String FIELD_SIGN = "sign";

    private final PayAssistService payAssistService;;

    /**
     * 签名
     */
    public void verifySign(PayCommonParam param) {
        // 先触发一下平台配置上下文的初始化
        payAssistService.initPlatform();
        ApiInfoLocal apiInfo = PaymentContextLocal.get().getApiInfo();
        PlatformLocal platform = PaymentContextLocal.get().getPlatform();

        // 判断当前接口是否不需要签名
        if (!apiInfo.isReqSign()){
            return;
        }
        // 参数转换为Map对象
        Map<String, String> params = param.toMap();
        String signType = platform.getSignType();
        // 生成签名前先去除sign
        params.remove(FIELD_SIGN);
        String data = PayKit.createLinkString(params);
        if (Objects.equals(PaySignTypeEnum.HMAC_SHA256.getCode(), signType)){
            // 签名验证
            data += "&key=" + platform.getSignSecret();
            String sha256 = PayKit.hmacSha256(data, platform.getSignSecret());
            if (!Objects.equals(sha256, params.get(FIELD_SIGN))){
                throw new PayFailureException("签名验证未通过");
            }
        } else if (Objects.equals(PaySignTypeEnum.MD5.getCode(), signType)){
            data += "&key=" + platform.getSignSecret();
            String md5 = PayKit.md5(data.toUpperCase());
            String sign = StrUtil.toString(params.get(FIELD_SIGN));
            // 签名验证
            if (!md5.equalsIgnoreCase(sign)){
                throw new PayFailureException("签名验证未通过");
            }
        } else {
            throw new PayFailureException("签名方式错误");
        }
    }

}
