package com.mangzai.curiotrinketbridge.embeddedacce.pond;

import com.mangzai.curiotrinketbridge.embeddedacce.api.AccessoriesCapability;
import com.mangzai.curiotrinketbridge.embeddedacce.api.AccessoriesHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface AccessoriesAPIAccess {

    @Nullable
    default AccessoriesCapability accessoriesCapability() {
        throw new IllegalStateException("[AccessoriesAPIAccess]: Default interface method not implemented!");
    }

    @Nullable
    default AccessoriesHolder accessoriesHolder() {
        throw new IllegalStateException("[AccessoriesAPIAccess]: Default interface method not implemented!");
    }
}
