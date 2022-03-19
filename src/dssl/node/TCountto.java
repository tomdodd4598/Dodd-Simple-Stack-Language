/* This file was generated by SableCC (http://www.sablecc.org/). */

package dssl.node;

import dssl.analysis.*;

@SuppressWarnings("nls")
public final class TCountto extends Token
{
    public TCountto()
    {
        super.setText("countto");
    }

    public TCountto(int line, int pos)
    {
        super.setText("countto");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TCountto(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTCountto(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TCountto text.");
    }
}
