package com.beyt.anouncy.persist.swagger.config;

import com.beyt.anouncy.persist.swagger.annotation.EnableGrpcSwagger;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.lang.Nullable;

public class GrpcSwaggerSelector extends AdviceModeImportSelector<EnableGrpcSwagger> {

    @Nullable
    @Override
    protected String[] selectImports(AdviceMode adviceMode) {
        return new String[]{
                GrpcSwaggerConfiguration.class.getName()
        };
    }
}
