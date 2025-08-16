package com.wiredi.jpa;

import com.wiredi.runtime.WiredApplication;
import com.wiredi.runtime.WiredApplicationInstance;

public class Main {

    public static void main(String[] args) {
        WiredApplicationInstance applicationInstance = WiredApplication.start();
        applicationInstance.awaitCompletion();
    }
}
