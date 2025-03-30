package ru.avmakarov.parser;

public interface LogLineParser {

    /**
     * Разбор строки вида
     * 192.168.32.181 - - [14/06/2017:16:47:02 +1000] "PUT /rest/v1.4/documents?zone=default&_rid=6076537c HTTP/1.1" 200 2 44.510983 "-" "@list-item-updater" prio:0
     * @param line Входящая строка
     * @return Полезная информация для анализа
     */
    LineInfo parse(String line);

    record LineInfo(String timestamp, int status, Double responseTime) {
    }
}
