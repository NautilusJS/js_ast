package com.mindlin.jsast.fs;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.mindlin.jsast.impl.util.CharacterStream;

public class ZipEntrySourceFile implements SourceFile {
	public static List<ZipEntrySourceFile> read(Path zipPath, Charset charset) throws IOException {
		List<ZipEntrySourceFile> sources = new ArrayList<>();
		
		try (ZipFile zip = new ZipFile(zipPath.toAbsolutePath().toFile(), charset)) {
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				String entryName = entry.getName();
				if (entryName.endsWith(".js") || entryName.endsWith(".ts"))
					sources.add(new ZipEntrySourceFile(entry, charset));
			}
		}
		
		return sources;
	}
	
	protected ZipEntry entry;
	protected Charset charset;
	
	public ZipEntrySourceFile(ZipEntry entry, Charset charset) {
		
	}
	
	@Override
	public Path getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CharacterStream getSourceStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long[] lineOffsets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Path getOriginalPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isExtern() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
