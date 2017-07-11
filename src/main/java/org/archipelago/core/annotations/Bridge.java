package org.archipelago.core.annotations;

import java.lang.annotation.*;

/**
 * Created by Gilles Bodart on 18/08/2016.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Bridge {

    Class<?> descriptor();

}
