/* This file was generated by SableCC (http://www.sablecc.org/). */

package dssl.node;

import dssl.analysis.*;

@SuppressWarnings("nls")
public final class TMacro extends Token
{
    public TMacro()
    {
        super.setText("macro");
    }

    public TMacro(int line, int pos)
    {
        super.setText("macro");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TMacro(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTMacro(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TMacro text.");
    }
}
