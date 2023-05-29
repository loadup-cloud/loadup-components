package om.github.loadup.components.captcha.service;

import java.io.OutputStream;

public interface CaptchaService {
    /**
     * 生成验证码
     *
     * @param uuid         自定义缓存的 uuid
     * @param outputStream OutputStream
     */
    void generate(String uuid, OutputStream outputStream);

    /**
     * 校验验证码
     *
     * @param uuid             自定义缓存的 uuid
     * @param userInputCaptcha 用户输入的图形验证码
     * @return 是否校验成功
     */
    boolean validate(String uuid, String userInputCaptcha);
}
