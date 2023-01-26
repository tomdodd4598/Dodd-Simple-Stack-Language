package dssl.interpret;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.magic.Magic;

public class ScopeMaps {
	
	public final Map<@NonNull String, Def> defMap;
	public final Map<@NonNull String, Clazz> clazzMap;
	public final Map<@NonNull String, Magic> magicMap;
	
	public ScopeMaps(Map<@NonNull String, Def> defMap, Map<@NonNull String, Clazz> clazzMap, Map<@NonNull String, Magic> magicMap) {
		this.defMap = defMap;
		this.clazzMap = clazzMap;
		this.magicMap = magicMap;
	}
	
	public static ScopeMaps empty() {
		return new ScopeMaps(new HashMap<>(), new HashMap<>(), new HashMap<>());
	}
	
	@Override
	public ScopeMaps clone() {
		return new ScopeMaps(new HashMap<>(defMap), new HashMap<>(clazzMap), new HashMap<>(magicMap));
	}
}
