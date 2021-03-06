/*********************************************************************************
 *                                                                               *
 * The MIT License                                                               *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org and other contributors.                      *
 *                                                                               *
 * Permission is hereby granted, free of charge, to any person obtaining a copy  *
 * of this software and associated documentation files (the "Software"), to deal *
 * in the Software without restriction, including without limitation the rights  *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     *
 * copies of the Software, and to permit persons to whom the Software is         *
 * furnished to do so, subject to the following conditions:                      *
 *                                                                               *
 * The above copyright notice and this permission notice shall be included in    *
 * all copies or substantial portions of the Software.                           *
 *                                                                               *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     *
 * THE SOFTWARE.                                                                 *
 ********************************************************************************/
package org.aoju.bus.health.hardware.unix.solaris;

import org.aoju.bus.health.Builder;
import org.aoju.bus.health.Command;
import org.aoju.bus.health.Memoizer;
import org.aoju.bus.health.hardware.AbstractVirtualMemory;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Memory obtained by kstat and swap
 *
 * @author Kimi Liu
 * @version 5.8.1
 * @since JDK 1.8+
 */
public class SolarisVirtualMemory extends AbstractVirtualMemory {

    private static final Pattern SWAP_INFO = Pattern.compile(".+\\s(\\d+)K\\s+(\\d+)K$");

    private final Supplier<SwapInfo> swapInfo = Memoizer.memoize(this::querySwapInfo, Memoizer.defaultExpiration());

    private final Supplier<Long> pagesIn = Memoizer.memoize(this::queryPagesIn, Memoizer.defaultExpiration());

    private final Supplier<Long> pagesOut = Memoizer.memoize(this::queryPagesOut, Memoizer.defaultExpiration());

    @Override
    public long getSwapUsed() {
        return swapInfo.get().used;
    }

    @Override
    public long getSwapTotal() {
        return swapInfo.get().total;
    }

    @Override
    public long getSwapPagesIn() {
        return pagesIn.get();
    }

    @Override
    public long getSwapPagesOut() {
        return pagesOut.get();
    }

    private long queryPagesIn() {
        long swapPagesIn = 0L;
        for (String s : Command.runNative("kstat -p cpu_stat:::pgpgin")) {
            swapPagesIn += Builder.parseLastLong(s, 0L);
        }
        return swapPagesIn;
    }

    private long queryPagesOut() {
        long swapPagesOut = 0L;
        for (String s : Command.runNative("kstat -p cpu_stat:::pgpgout")) {
            swapPagesOut += Builder.parseLastLong(s, 0L);
        }
        return swapPagesOut;
    }

    private SwapInfo querySwapInfo() {
        long swapTotal = 0L;
        long swapUsed = 0L;
        String swap = Command.getAnswerAt("swap -lk", 1);
        Matcher m = SWAP_INFO.matcher(swap);
        if (m.matches()) {
            swapTotal = Builder.parseLongOrDefault(m.group(1), 0L) << 10;
            swapUsed = swapTotal - (Builder.parseLongOrDefault(m.group(2), 0L) << 10);
        }
        return new SwapInfo(swapTotal, swapUsed);
    }

    private static final class SwapInfo {
        private final long total;
        private final long used;

        private SwapInfo(long total, long used) {
            this.total = total;
            this.used = used;
        }
    }

}
