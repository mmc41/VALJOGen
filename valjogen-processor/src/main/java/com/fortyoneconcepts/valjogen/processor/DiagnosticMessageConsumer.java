/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.processor;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

/**
 * Consumer of messages like errors and warnings that the annotation processor should make note of.
 *
 * @author mmc
 */
@FunctionalInterface
public interface DiagnosticMessageConsumer
{
    /**
     * Sends the message to the annotation processor.
     *
     * @param element the element (class/method/field etc.) that the message is related to.
     * @param messageKind the type of message (error, warning etc).
     * @param message the message).
     *
     */
    public void message(Element element, Kind messageKind, String message);
}
