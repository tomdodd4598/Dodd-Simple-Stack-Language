/* This file was generated by SableCC (http://www.sablecc.org/). */

package dssl.node;

import dssl.analysis.*;

@SuppressWarnings("nls")
public final class TPut extends Token
{
    public TPut()
    {
        super.setText("put");
    }

    public TPut(int line, int pos)
    {
        super.setText("put");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TPut(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTPut(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TPut text.");
    }
}
