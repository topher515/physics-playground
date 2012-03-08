package gui.opf;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( ElementType.METHOD )
@Retention( RetentionPolicy.RUNTIME )
public @interface Setter
{
	// This annotation tells the ObjectPropertiesFrame which methods to make
	// components for.
	OPFComponentType componentType() default OPFComponentType.Auto;

	boolean showInOPF() default true;
	//boolean hasGetter() default true;

	String getter() default "";	//Can't default this to null. :(
	String eventType() default "Properties";

}
