/* This file was generated by SableCC (http://www.sablecc.org/). */

package dssl.node;

import dssl.analysis.*;

@SuppressWarnings("nls")
public final class TDict extends Token
{
    public TDict()
    {
        super.setText("dict");
    }

    public TDict(int line, int pos)
    {
        super.setText("dict");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TDict(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTDict(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TDict text.");
    }
}
