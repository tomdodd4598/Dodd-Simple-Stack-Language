package dssl.interpret.element.iter;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.TokenExecutor;
import dssl.interpret.element.*;

public class ChunksIterElement extends IterElement {
	
	protected final IterElement internal;
	protected final long size;
	
	protected @Nullable ListElement next = null;
	protected boolean end = false;
	
	public ChunksIterElement(IterElement internal, long size) {
		super();
		this.internal = internal;
		this.size = size;
	}
	
	protected void prepare(TokenExecutor exec) {
		if (next == null && !end) {
			long count = 0;
			List<@NonNull Element> list = new ArrayList<>();
			
			while (internal.hasNext(exec)) {
				if (count++ >= size) {
					next = new ListElement(list);
					return;
				}
				list.add(internal.next(exec));
			}
			
			end = true;
		}
	}
	
	@Override
	public boolean hasNext(TokenExecutor exec) {
		prepare(exec);
		return !end;
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Element next(TokenExecutor exec) {
		prepare(exec);
		@NonNull Element elem = next;
		next = null;
		return elem;
	}
}
