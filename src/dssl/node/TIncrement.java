/* This file was generated by SableCC (http://www.sablecc.org/). */

package dssl.node;

import dssl.analysis.*;

@SuppressWarnings("nls")
public final class TIncrement extends Token
{
    public TIncrement()
    {
        super.setText("++");
    }

    public TIncrement(int line, int pos)
    {
        super.setText("++");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TIncrement(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTIncrement(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TIncrement text.");
    }
}
