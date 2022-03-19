/* This file was generated by SableCC (http://www.sablecc.org/). */

package dssl.node;

import dssl.analysis.*;

@SuppressWarnings("nls")
public final class TLeftShiftEquals extends Token
{
    public TLeftShiftEquals()
    {
        super.setText("<<=");
    }

    public TLeftShiftEquals(int line, int pos)
    {
        super.setText("<<=");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TLeftShiftEquals(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTLeftShiftEquals(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TLeftShiftEquals text.");
    }
}
