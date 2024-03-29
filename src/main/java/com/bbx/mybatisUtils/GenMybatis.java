package com.bbx.mybatisUtils;

import freemarker.template.TemplateException;
import org.apache.commons.cli.*;
import java.io.*;
import java.net.URL;
import java.sql.*;

public class GenMybatis {

    public static void main(String[] args) {
        String cfg_file = "config.ini";

        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption("f", "file", true, "file 配置文件");

        try {

            CommandLine commandLine = parser.parse(options, args);
            if (commandLine.hasOption("f")){
                cfg_file = commandLine.getOptionValue("f");
            }

            URL url  = GenMybatis.class.getProtectionDomain().getCodeSource().getLocation();
            String path = java.net.URLDecoder.decode(url.getPath(), "utf-8");

            GenMybatisHandle gms = new GenMybatisHandle();
            gms.gen(path  + cfg_file, path);

        } catch (ParseException | IOException | SQLException | TemplateException e) {
            e.printStackTrace();
        }

    }


}
