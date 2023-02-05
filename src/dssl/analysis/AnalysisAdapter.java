/* This file was generated by SableCC (http://www.sablecc.org/). */

package dssl.analysis;

import java.util.*;
import dssl.node.*;

public class AnalysisAdapter implements Analysis
{
    private Hashtable<Node,Object> in;
    private Hashtable<Node,Object> out;

    @Override
    public Object getIn(Node node)
    {
        if(this.in == null)
        {
            return null;
        }

        return this.in.get(node);
    }

    @Override
    public void setIn(Node node, Object o)
    {
        if(this.in == null)
        {
            this.in = new Hashtable<Node,Object>(1);
        }

        if(o != null)
        {
            this.in.put(node, o);
        }
        else
        {
            this.in.remove(node);
        }
    }

    @Override
    public Object getOut(Node node)
    {
        if(this.out == null)
        {
            return null;
        }

        return this.out.get(node);
    }

    @Override
    public void setOut(Node node, Object o)
    {
        if(this.out == null)
        {
            this.out = new Hashtable<Node,Object>(1);
        }

        if(o != null)
        {
            this.out.put(node, o);
        }
        else
        {
            this.out.remove(node);
        }
    }

    @Override
    public void caseTBlank(TBlank node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTComment(TComment node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTLBrace(TLBrace node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRBrace(TRBrace node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTLBracket(TLBracket node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRBracket(TRBracket node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTInclude(TInclude node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTImport(TImport node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTNative(TNative node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTDef(TDef node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTMacro(TMacro node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTClass(TClass node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTMagic(TMagic node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTNew(TNew node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTDeref(TDeref node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTExch(TExch node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTPop(TPop node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTDup(TDup node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTClone(TClone node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRoll(TRoll node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRid(TRid node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTCopy(TCopy node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTIndex(TIndex node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTCount(TCount node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTCountto(TCountto node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRead(TRead node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTPrint(TPrint node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTPrintln(TPrintln node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTInterpret(TInterpret node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTInt(TInt node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTBool(TBool node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTFloat(TFloat node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTChar(TChar node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTString(TString node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRange(TRange node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTList(TList node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTTuple(TTuple node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTSet(TSet node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTDict(TDict node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTNull(TNull node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTHash(THash node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTForeach(TForeach node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTUnpack(TUnpack node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTSize(TSize node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTEmpty(TEmpty node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTContains(TContains node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTAdd(TAdd node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRemove(TRemove node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTContainsall(TContainsall node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTAddall(TAddall node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRemoveall(TRemoveall node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTClear(TClear node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTGet(TGet node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTPut(TPut node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTPutall(TPutall node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTContainskey(TContainskey node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTContainsvalue(TContainsvalue node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTContainsentry(TContainsentry node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTKeys(TKeys node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTValues(TValues node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTEntries(TEntries node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTType(TType node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTCast(TCast node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTExec(TExec node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTIf(TIf node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTIfelse(TIfelse node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRepeat(TRepeat node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTLoop(TLoop node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTQuit(TQuit node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTContinue(TContinue node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTBreak(TBreak node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTEquals(TEquals node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTIncrement(TIncrement node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTDecrement(TDecrement node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTPlusEquals(TPlusEquals node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTAndEquals(TAndEquals node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTOrEquals(TOrEquals node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTXorEquals(TXorEquals node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTMinusEquals(TMinusEquals node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTConcatEquals(TConcatEquals node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTLeftShiftEquals(TLeftShiftEquals node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRightShiftEquals(TRightShiftEquals node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTMultiplyEquals(TMultiplyEquals node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTDivideEquals(TDivideEquals node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRemainderEquals(TRemainderEquals node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTPowerEquals(TPowerEquals node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTIdivideEquals(TIdivideEquals node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTModuloEquals(TModuloEquals node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTEqualTo(TEqualTo node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTNotEqualTo(TNotEqualTo node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTLessThan(TLessThan node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTLessOrEqual(TLessOrEqual node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTMoreThan(TMoreThan node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTMoreOrEqual(TMoreOrEqual node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTPlus(TPlus node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTAnd(TAnd node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTOr(TOr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTXor(TXor node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTMinus(TMinus node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTConcat(TConcat node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTLeftShift(TLeftShift node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRightShift(TRightShift node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTMultiply(TMultiply node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTDivide(TDivide node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRemainder(TRemainder node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTPower(TPower node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTIdivide(TIdivide node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTModulo(TModulo node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTNot(TNot node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTNeg(TNeg node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTIntValue(TIntValue node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTBoolValue(TBoolValue node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTFloatValue(TFloatValue node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTCharValue(TCharValue node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTLineStringValue(TLineStringValue node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTBlockStringValue(TBlockStringValue node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTIdentifier(TIdentifier node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTLabel(TLabel node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTMember(TMember node)
    {
        defaultCase(node);
    }

    @Override
    public void caseEOF(EOF node)
    {
        defaultCase(node);
    }

    @Override
    public void caseInvalidToken(InvalidToken node)
    {
        defaultCase(node);
    }

    public void defaultCase(@SuppressWarnings("unused") Node node)
    {
        // do nothing
    }
}
