/* This file was generated by SableCC (http://www.sablecc.org/). */

package dssl.node;

import dssl.analysis.*;

@SuppressWarnings("nls")
public final class TStackindex extends Token
{
    public TStackindex()
    {
        super.setText("stackindex");
    }

    public TStackindex(int line, int pos)
    {
        super.setText("stackindex");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TStackindex(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTStackindex(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TStackindex text.");
    }
}