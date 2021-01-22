package com.neotys.qtest.webhook.Logger;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.neotys.qtest.webhook.common.Constants.LOGING_LEVEL_KEY;


public  class NeoLoadLogger {

    private String logginglevel;
    private String testid;
    private Logger loger;
    private ConsoleHandler handler;
    public static final String INFO="INFO";
    public static final String WARN="WARN";
    public static final String ERROR="ERROR";
    public static final String TRACE="TRACE";
    public static final String DEBUG="DEBUG";

   String messageFormat="{" +
            "  \"logLevel\": \"%s\"," +
            "  \"neoload-test-id\": \"%s\"," +
            "  \"message\": \"%s\"" +
            "}\n";

    public String getTestid() {
        return testid;
    }

    public void setTestid(String testid) {
        this.testid = testid;
        handler.setFormatter(new NeoLoadLoggerFormater(testid));
    }

    public NeoLoadLogger(String className) {
        handler = new ConsoleHandler();
        loger = Logger.getLogger(className);
        loger.setUseParentHandlers(false);
        loger.addHandler(handler);
        handler.setLevel(getLevel());
        loger.setLevel(getLevel());

    }

    private void logenv(Map<String,String> env)
    {
        StringBuilder sb= new StringBuilder();
        Iterator<Map.Entry<String, String>> itr = env.entrySet().iterator();
        System.out.println("Environement available from neoload service");
        while (itr.hasNext()) {
            Map.Entry<String, String> entry = itr.next();
            sb.append(entry.getKey());
            sb.append('=').append('"');
            sb.append(entry.getValue());
            sb.append('"');
            if (itr.hasNext()) {
                sb.append(',').append(' ');
            }
        }
        System.out.println(sb.toString());
    }


    private Level getLevel()
    {

        String level=System.getenv(LOGING_LEVEL_KEY);
        switch (level.toUpperCase())
        {
            case INFO:
                return Level.INFO;
            case ERROR:
                return Level.SEVERE;
            case DEBUG:
                return Level.FINE;
            case TRACE:
                return Level.FINEST;
            case WARN:
                return Level.WARNING;
            default: return Level.SEVERE;
        }
    }

    public void error(String s) {
      loger.log(Level.SEVERE,s);
    }
    public void error(String s,Throwable throwable) {
        loger.log(Level.SEVERE,s +" exception : "+ throwable.getMessage(),throwable);
        throwable.printStackTrace();
    }

    public void info(String s) {
        loger.log(Level.INFO,s);
    }

    public void warn(String s) {
        loger.log(Level.WARNING,s);
    }


    public void debug(String s) {
        loger.log(Level.FINE,s);
    }


    public void trace(String s) {
        loger.log(Level.FINEST,s);
    }

    public void debugfileContent(String file)
    {
        try {
            debug("content of the file : "+file);
            List<String> allLines = Files.readAllLines(Paths.get(file));
           for (String line : allLines) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
