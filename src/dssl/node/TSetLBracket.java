/* This file was generated by SableCC (http://www.sablecc.org/). */

package dssl.node;

import dssl.analysis.*;

@SuppressWarnings("nls")
public final class TSetLBracket extends Token
{
    public TSetLBracket()
    {
        super.setText("(|");
    }

    public TSetLBracket(int line, int pos)
    {
        super.setText("(|");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TSetLBracket(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTSetLBracket(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TSetLBracket text.");
    }
}
