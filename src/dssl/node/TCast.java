/* This file was generated by SableCC (http://www.sablecc.org/). */

package dssl.node;

import dssl.analysis.*;

@SuppressWarnings("nls")
public final class TCast extends Token
{
    public TCast()
    {
        super.setText("cast");
    }

    public TCast(int line, int pos)
    {
        super.setText("cast");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TCast(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTCast(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TCast text.");
    }
}
