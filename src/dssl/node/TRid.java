/* This file was generated by SableCC (http://www.sablecc.org/). */

package dssl.node;

import dssl.analysis.*;

@SuppressWarnings("nls")
public final class TRid extends Token
{
    public TRid()
    {
        super.setText("rid");
    }

    public TRid(int line, int pos)
    {
        super.setText("rid");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TRid(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTRid(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TRid text.");
    }
}
