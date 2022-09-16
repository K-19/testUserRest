package by.k19.usercore.controller;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public abstract class LoggingController {
    public String getRemoteAddress(HttpServletRequest request) {
        return request.getRemoteHost() + ":" + request.getRemotePort();
    }
}
