/* This file was generated by SableCC (http://www.sablecc.org/). */

package dssl.node;

import dssl.analysis.*;

@SuppressWarnings("nls")
public final class TEntries extends Token
{
    public TEntries()
    {
        super.setText("entries");
    }

    public TEntries(int line, int pos)
    {
        super.setText("entries");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TEntries(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTEntries(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TEntries text.");
    }
}
