package net.william278.cloplib.listener;

import net.william278.cloplib.operation.OperationPosition;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

/**
 * An interface for providing callbacks for inspection actions.
 *
 * <p>
 * This should be used for inspecting chunks with tools such as sticks to view borders,
 * as well as for claim creation actions such as with a golden shovel
 * @since 1.0
 */
public interface InspectorCallbackProvider {

    /**
     * Get the distance that inspectors check for
     *
     * @return the distance
     * @since 1.0
     */
    int getInspectionDistance();

    /**
     * Set the distance that inspectors check for
     *
     * @param distance the distance to set
     * @since 1.0
     */
    void setInspectionDistance(int distance);

    /**
     * Get handler callbacks for inspection actions
     *
     * @return the handlers
     * @since 1.0
     */
    @NotNull
    Map<String, Consumer<OperationPosition>> getInspectionHandlers();

    /**
     * Sets a callback for use handling inspection actions
     *
     * @param material the material to set the callback for
     * @param callback the callback to set
     * @since 1.0
     */
    void setInspectorCallback(@NotNull String material, @NotNull Consumer<OperationPosition> callback);

}
