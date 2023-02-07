/* This file was generated by SableCC (http://www.sablecc.org/). */

package dssl.analysis;

import dssl.node.*;

public interface Analysis extends Switch
{
    Object getIn(Node node);
    void setIn(Node node, Object o);
    Object getOut(Node node);
    void setOut(Node node, Object o);

    void caseTBlank(TBlank node);
    void caseTComment(TComment node);
    void caseTLBrace(TLBrace node);
    void caseTRBrace(TRBrace node);
    void caseTLBracket(TLBracket node);
    void caseTRBracket(TRBracket node);
    void caseTInclude(TInclude node);
    void caseTImport(TImport node);
    void caseTNative(TNative node);
    void caseTDef(TDef node);
    void caseTMacro(TMacro node);
    void caseTClass(TClass node);
    void caseTMagic(TMagic node);
    void caseTNew(TNew node);
    void caseTDeref(TDeref node);
    void caseTExch(TExch node);
    void caseTPop(TPop node);
    void caseTDup(TDup node);
    void caseTClone(TClone node);
    void caseTRoll(TRoll node);
    void caseTRid(TRid node);
    void caseTCopy(TCopy node);
    void caseTIndex(TIndex node);
    void caseTCount(TCount node);
    void caseTCountto(TCountto node);
    void caseTRead(TRead node);
    void caseTPrint(TPrint node);
    void caseTPrintln(TPrintln node);
    void caseTInterpret(TInterpret node);
    void caseTInt(TInt node);
    void caseTBool(TBool node);
    void caseTFloat(TFloat node);
    void caseTChar(TChar node);
    void caseTString(TString node);
    void caseTRange(TRange node);
    void caseTList(TList node);
    void caseTTuple(TTuple node);
    void caseTSet(TSet node);
    void caseTDict(TDict node);
    void caseTNull(TNull node);
    void caseTHash(THash node);
    void caseTForeach(TForeach node);
    void caseTUnpack(TUnpack node);
    void caseTSize(TSize node);
    void caseTEmpty(TEmpty node);
    void caseTContains(TContains node);
    void caseTAdd(TAdd node);
    void caseTRemove(TRemove node);
    void caseTContainsall(TContainsall node);
    void caseTAddall(TAddall node);
    void caseTRemoveall(TRemoveall node);
    void caseTClear(TClear node);
    void caseTGet(TGet node);
    void caseTPut(TPut node);
    void caseTPutall(TPutall node);
    void caseTContainskey(TContainskey node);
    void caseTContainsvalue(TContainsvalue node);
    void caseTContainsentry(TContainsentry node);
    void caseTKeys(TKeys node);
    void caseTValues(TValues node);
    void caseTEntries(TEntries node);
    void caseTType(TType node);
    void caseTCast(TCast node);
    void caseTExec(TExec node);
    void caseTIf(TIf node);
    void caseTIfelse(TIfelse node);
    void caseTRepeat(TRepeat node);
    void caseTLoop(TLoop node);
    void caseTQuit(TQuit node);
    void caseTContinue(TContinue node);
    void caseTBreak(TBreak node);
    void caseTEquals(TEquals node);
    void caseTIncrement(TIncrement node);
    void caseTDecrement(TDecrement node);
    void caseTPlusEquals(TPlusEquals node);
    void caseTAndEquals(TAndEquals node);
    void caseTOrEquals(TOrEquals node);
    void caseTXorEquals(TXorEquals node);
    void caseTMinusEquals(TMinusEquals node);
    void caseTConcatEquals(TConcatEquals node);
    void caseTLeftShiftEquals(TLeftShiftEquals node);
    void caseTRightShiftEquals(TRightShiftEquals node);
    void caseTMultiplyEquals(TMultiplyEquals node);
    void caseTDivideEquals(TDivideEquals node);
    void caseTRemainderEquals(TRemainderEquals node);
    void caseTPowerEquals(TPowerEquals node);
    void caseTIdivideEquals(TIdivideEquals node);
    void caseTModuloEquals(TModuloEquals node);
    void caseTEqualTo(TEqualTo node);
    void caseTNotEqualTo(TNotEqualTo node);
    void caseTLessThan(TLessThan node);
    void caseTLessOrEqual(TLessOrEqual node);
    void caseTMoreThan(TMoreThan node);
    void caseTMoreOrEqual(TMoreOrEqual node);
    void caseTPlus(TPlus node);
    void caseTAnd(TAnd node);
    void caseTOr(TOr node);
    void caseTXor(TXor node);
    void caseTMinus(TMinus node);
    void caseTConcat(TConcat node);
    void caseTLeftShift(TLeftShift node);
    void caseTRightShift(TRightShift node);
    void caseTMultiply(TMultiply node);
    void caseTDivide(TDivide node);
    void caseTRemainder(TRemainder node);
    void caseTPower(TPower node);
    void caseTIdivide(TIdivide node);
    void caseTModulo(TModulo node);
    void caseTNot(TNot node);
    void caseTIntValue(TIntValue node);
    void caseTBoolValue(TBoolValue node);
    void caseTFloatValue(TFloatValue node);
    void caseTCharValue(TCharValue node);
    void caseTLineStringValue(TLineStringValue node);
    void caseTBlockStringValue(TBlockStringValue node);
    void caseTIdentifier(TIdentifier node);
    void caseTLabel(TLabel node);
    void caseTMember(TMember node);
    void caseTModule(TModule node);
    void caseEOF(EOF node);
    void caseInvalidToken(InvalidToken node);
}
