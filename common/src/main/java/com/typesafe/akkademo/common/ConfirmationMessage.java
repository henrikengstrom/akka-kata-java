/**
 *  Copyright (C) 2011-2013 Typesafe, Inc <http://typesafe.com>
 */
package com.typesafe.akkademo.common;

import java.io.Serializable;

public class ConfirmationMessage implements Serializable {
    private final int id;

    public ConfirmationMessage(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
