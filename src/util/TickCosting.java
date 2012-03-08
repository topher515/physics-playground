package util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used within Core for scheduling and execution priority.  Not used yet outside of core.
 * 
 * @author The UC Regents
 *
 */
@Target( ElementType.METHOD )
@Retention( RetentionPolicy.RUNTIME )
public @interface TickCosting
{
	int StartCost() default 100;
}
