/* This file was generated by SableCC (http://www.sablecc.org/). */

package dssl.node;

import dssl.analysis.*;

@SuppressWarnings("nls")
public final class TAddall extends Token
{
    public TAddall()
    {
        super.setText("addall");
    }

    public TAddall(int line, int pos)
    {
        super.setText("addall");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TAddall(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTAddall(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TAddall text.");
    }
}
