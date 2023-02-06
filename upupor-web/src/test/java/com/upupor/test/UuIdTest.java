/*
 * MIT License
 *
 * Copyright (c) 2021-2022 yangrunkang
 *
 * Author: yangrunkang
 * Email: yangrunkang53@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.upupor.test;

import com.upupor.framework.utils.CcUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Yang Runkang (cruise)
 * @date 2023年02月06日 10:06
 * @email: yangrunkang53@gmail.com
 */
public class UuIdTest {

    public static void main(String[] args) {
        HashSet<String> hashSet = new HashSet<>();


        List<CompletableFuture<Void>> voidCompletableFutureList = new ArrayList<>();

        for (int i = 0; i < 190; i++) {
            CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
                for (int j = 0; j < 100_0000; j++) {
                    String uuId = CcUtils.getUuId();
                    if (hashSet.contains(uuId)) {
                        System.out.println("重复:" + uuId);
                    } else {
//                        if (hashSet.size() % 100_0000 == 0) {
//                            System.out.println(Thread.currentThread().getName() + "-----> hashSet大小：" + hashSet.size());
//                        }
                        hashSet.add(uuId);
                    }
                }
            });
            voidCompletableFutureList.add(voidCompletableFuture);
        }

        CompletableFuture.allOf(voidCompletableFutureList.toArray(new CompletableFuture[0])).join();


        System.out.println("hashSet大小：" + hashSet.size());

    }

}
