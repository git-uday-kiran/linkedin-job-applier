package com.bitbees.jobapplier.linkedinjobapplier.models;

import java.util.Objects;

public record Page(String url) {

    public Page {
        Objects.requireNonNull(url, "Page url can not be ull");
    }

    public static Page of(String url) {
        return new Page(url);
    }

    public record Config(boolean onNewTab) {
    }
}
