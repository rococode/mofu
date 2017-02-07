package com.edasaki.misakachan.multithread;

/**
 * A condition that must be met for a callable to be executed.
 * @author Edasaki
 *
 */
@FunctionalInterface
public interface TaskCondition {
    public boolean shouldExecute();
}
