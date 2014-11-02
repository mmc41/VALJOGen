package com.fortyoneconcepts.valjogen.model.util;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

public final class IndentedPrintWriter extends PrintWriter
{
    private char[] current;

    private final String indent = "  ";
    private StringBuilder builder = new StringBuilder();
    private boolean empty = true;

    public IndentedPrintWriter(Writer writer)
    {
        super(writer);
    }

    public IndentedPrintWriter(OutputStream out)
    {
        this(new OutputStreamWriter(out));
    }

    @Override
    public void write(char[] buf, int offset, int count) {
        int lineStart = offset;
        int lineEnd = offset;
        final int bufferEnd = offset + count;
        while (lineEnd < bufferEnd)
        {
            char ch = buf[lineEnd++];
            if (ch == '\n') {
                writeIndent();
                super.write(buf, lineStart, lineEnd - lineStart);
                lineStart = lineEnd;
                empty = true;
            }
        }

        if (lineStart != lineEnd) {
            writeIndent();
            super.write(buf, lineStart, lineEnd - lineStart);
        }
    }

    @Override
    public void write(String s, int offset, int count)
    {
    	 int lineStart = offset;
         int lineEnd = offset;
         final int bufferEnd = offset + count;
         while (lineEnd < bufferEnd)
         {
             char ch = s.charAt(lineEnd++);
             if (ch == '\n') {
                 writeIndent();
                 super.write(s, lineStart, lineEnd - lineStart);
                 lineStart = lineEnd;
                 empty = true;
             }
         }

         if (lineStart != lineEnd) {
             writeIndent();
             super.write(s, lineStart, lineEnd - lineStart);
         }
    }

    @Override
    public void println() {
    	super.println();
    	empty=true;
    }

    private void writeIndent()
    {
        if (empty) {
            empty = false;
            if (builder.length() != 0) {
                if (current == null) {
                    current = builder.toString().toCharArray();
                }
                super.write(current, 0, current.length);
            }
        }
    }

    public void ensureNewLine()
    {
    	if (!empty)
    		println();
    }

    public void increaseIndent()
    {
        current = null;
        builder.append(indent);
    }

    public void decreaseIndent()
    {
        current = null;
        builder.delete(0, indent.length());
    }
}
