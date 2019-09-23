/*
 * The MIT License
 *
 * Copyright (c) 2017 aoju.org All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aoju.bus.crypto.symmetric;

import org.aoju.bus.core.consts.ModeType;
import org.aoju.bus.core.utils.StringUtils;
import org.aoju.bus.crypto.CryptoUtils;
import org.aoju.bus.crypto.Padding;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * DESede是由DES对称加密算法改进后的一种对称加密算法，又名3DES、TripleDES。
 * 使用 168 位的密钥对资料进行三次加密的一种机制；它通常（但非始终）提供极其强大的安全性。
 * 如果三个 56 位的子元素都相同，则三重 DES 向后兼容 DES。
 * Java中默认实现为：DESede/ECB/PKCS5Padding
 *
 * @author Kimi Liu
 * @version 3.5.6
 * @since JDK 1.8
 */
public class DESede extends Symmetric {

    /**
     * 构造，默认DESede/ECB/PKCS5Padding，使用随机密钥
     */
    public DESede() {
        super(ModeType.DESede);
    }

    /**
     * 构造，使用默认的DESede/ECB/PKCS5Padding
     *
     * @param key 密钥
     */
    public DESede(byte[] key) {
        super(ModeType.DESede, key);
    }

    /**
     * 构造，使用随机密钥
     *
     * @param mode    模式{@link ModeType}
     * @param padding {@link Padding}补码方式
     */
    public DESede(String mode, Padding padding) {
        this(mode, padding.name());
    }

    /**
     * 构造
     *
     * @param mode    模式{@link ModeType}
     * @param padding {@link Padding}补码方式
     * @param key     密钥，长度24位
     */
    public DESede(String mode, Padding padding, byte[] key) {
        this(mode, padding, key, null);
    }

    /**
     * 构造
     *
     * @param mode    模式{@link ModeType}
     * @param padding {@link Padding}补码方式
     * @param key     密钥，长度24位
     * @param iv      偏移向量，加盐
     * @since 3.3.0
     */
    public DESede(String mode, Padding padding, byte[] key, byte[] iv) {
        this(mode, padding.name(), key, iv);
    }

    /**
     * 构造
     *
     * @param mode    模式{@link ModeType}
     * @param padding {@link Padding}补码方式
     * @param key     密钥，长度24位
     * @since 3.3.0
     */
    public DESede(String mode, Padding padding, SecretKey key) {
        this(mode, padding, key, null);
    }

    /**
     * 构造
     *
     * @param mode    模式{@link ModeType}
     * @param padding {@link Padding}补码方式
     * @param key     密钥，长度24位
     * @param iv      偏移向量，加盐
     * @since 3.3.0
     */
    public DESede(String mode, Padding padding, SecretKey key, IvParameterSpec iv) {
        this(mode, padding.name(), key, iv);
    }

    /**
     * 构造
     *
     * @param mode    模式
     * @param padding 补码方式
     */
    public DESede(String mode, String padding) {
        this(mode, padding, (byte[]) null);
    }

    /**
     * 构造
     *
     * @param mode    模式
     * @param padding 补码方式
     * @param key     密钥，长度24位
     */
    public DESede(String mode, String padding, byte[] key) {
        this(mode, padding, key, null);
    }

    /**
     * 构造
     *
     * @param mode    模式
     * @param padding 补码方式
     * @param key     密钥，长度24位
     * @param iv      加盐
     */
    public DESede(String mode, String padding, byte[] key, byte[] iv) {
        this(mode, padding, CryptoUtils.generateKey(ModeType.DESede, key), null == iv ? null : new IvParameterSpec(iv));
    }

    /**
     * 构造
     *
     * @param mode    模式
     * @param padding 补码方式
     * @param key     密钥
     */
    public DESede(String mode, String padding, SecretKey key) {
        this(mode, padding, key, null);
    }

    /**
     * 构造
     *
     * @param mode    模式
     * @param padding 补码方式
     * @param key     密钥
     * @param iv      加盐
     */
    public DESede(String mode, String padding, SecretKey key, IvParameterSpec iv) {
        super(StringUtils.format("{}/{}/{}", ModeType.DESede, mode, padding), key, iv);
    }

    /**
     * 设置偏移向量
     *
     * @param iv {@link IvParameterSpec}偏移向量
     * @return 自身
     */
    public DESede setIv(IvParameterSpec iv) {
        super.setParams(iv);
        return this;
    }

    /**
     * 设置偏移向量
     *
     * @param iv 偏移向量，加盐
     * @return 自身
     * @since 3.3.0
     */
    public DESede setIv(byte[] iv) {
        setIv(new IvParameterSpec(iv));
        return this;
    }

}
