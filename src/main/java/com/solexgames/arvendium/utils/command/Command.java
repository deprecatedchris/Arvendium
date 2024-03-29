package com.solexgames.arvendium.utils.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = { ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Command {
	public String name();

	public String permission() default "";

	public String[] aliases() default {};

	public String description() default "";

	public String usage() default "";

	public boolean inGameOnly() default true;

	public boolean isAdminOnly() default false;
}
