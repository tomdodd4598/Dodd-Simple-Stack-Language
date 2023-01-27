/* This file was generated by SableCC (http://www.sablecc.org/). */

package dssl.node;

import dssl.analysis.*;

@SuppressWarnings("nls")
public final class TContainskey extends Token
{
    public TContainskey()
    {
        super.setText("containskey");
    }

    public TContainskey(int line, int pos)
    {
        super.setText("containskey");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TContainskey(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTContainskey(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TContainskey text.");
    }
}