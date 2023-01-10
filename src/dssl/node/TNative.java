/* This file was generated by SableCC (http://www.sablecc.org/). */

package dssl.node;

import dssl.analysis.*;

@SuppressWarnings("nls")
public final class TNative extends Token
{
    public TNative()
    {
        super.setText("native");
    }

    public TNative(int line, int pos)
    {
        super.setText("native");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TNative(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTNative(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TNative text.");
    }
}
