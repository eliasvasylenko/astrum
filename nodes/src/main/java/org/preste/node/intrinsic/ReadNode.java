package org.preste.node.intrinsic;

import java.util.function.IntPredicate;

import org.preste.reader.PresteData;
import org.preste.reader.PresteReader;
import org.preste.reader.ReaderMacro;
import org.preste.reader.ReadingContext;
import org.preste.scanner.Cursor;
import org.preste.scanner.Scanner;
import org.preste.type.DataLibrary;

import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotTypeException;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "read")
public abstract class ReadNode extends IntrinsicNode {
  private static class PresteDataWrapper implements PresteData {
    private final Object data;

    public PresteDataWrapper(Object data) {
      this.data = data;
    }

    public Object getValue() {
      return data;
    }
  }

  private static final DataLibrary dataLibrary = DataLibrary.getFactory().createDispatched(5);

  private final FrameSlot dynamicScopeSlot;
  private final FrameSlot readTableSlot;

  public ReadNode(FrameSlot dynamicScopeSlot, FrameSlot readTableSlot) {
    this.dynamicScopeSlot = dynamicScopeSlot;
    this.readTableSlot = readTableSlot;
  }

  @Override
  protected Object execute(VirtualFrame frame) {
    try {
      Object dynamicScope = frame.getObject(dynamicScopeSlot);
      DataLibrary dynamicScopeLibrary = DataLibrary.getFactory().create(dynamicScope);
      dynamicScopeLibrary.adoptChildren();

      Object readTable = frame.getObject(readTableSlot);
      DataLibrary readTableLibrary = DataLibrary.getFactory().create(readTable);
      readTableLibrary.adoptChildren();

      new PresteReader(new ReadingContext() {
        @Override
        public PresteData makeCons(Object car, Object cdr) {
          return new PresteDataWrapper(valueLibrary.consWith(cdr, car));
        }

        @Override
        public PresteData makeSymbol(String namespace, String name) {
          return new PresteDataWrapper(symbolLibrary);
        }

        @Override
        public ReaderMacro resolveReaderMacro(PresteData symbol) {
          return readTableLibrary.get(readTable, symbol);
        }
      }, new Scanner() {
        @Override
        public String takeBufferTo(long position) {
          // TODO Auto-generated method stub
          return null;
        }

        @Override
        public Cursor peekInput() {
          // TODO Auto-generated method stub
          return null;
        }

        @Override
        public long inputPosition() {
          // TODO Auto-generated method stub
          return 0;
        }

        @Override
        public void discardBufferTo(long position) {
          // TODO Auto-generated method stub

        }

        @Override
        public long bufferPosition() {
          // TODO Auto-generated method stub
          return 0;
        }

        @Override
        public Cursor advanceInputWhile(IntPredicate condition) {
          // TODO Auto-generated method stub
          return null;
        }

        @Override
        public Cursor advanceInput() {
          // TODO Auto-generated method stub
          return null;
        }
      });

      return null;
    } catch (FrameSlotTypeException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}