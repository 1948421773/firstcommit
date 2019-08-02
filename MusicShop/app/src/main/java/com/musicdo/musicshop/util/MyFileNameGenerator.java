package com.musicdo.musicshop.util;

import com.danikula.videocache.file.FileNameGenerator;

/**
 * Created by Yuedu on 2018/9/7.
 */

public class MyFileNameGenerator implements FileNameGenerator
{
    @Override
    public String generate(String url)
    {
        String name = url.substring(url.lastIndexOf("/") + 1, url.length());
        return name;
    }
}

