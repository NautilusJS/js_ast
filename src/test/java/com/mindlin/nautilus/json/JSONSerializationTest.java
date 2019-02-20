package com.mindlin.jsast.json;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import org.junit.Test;

import com.mindlin.jsast.json.api.JSONExternalizable;
import com.mindlin.jsast.json.api.JSONInput;
import com.mindlin.jsast.json.api.JSONObjectOutput;
import com.mindlin.jsast.json.api.JSONOutput;

public class JSONSerializationTest {

	@Test
	public void test() {
		StringWriter w = new StringWriter();
		JSONOutputStream str = new JSONOutputStream(w);
		str.config.wrapObjectEntries = true;
//		str.config.wrapArrayEntries = true;
		str.config.pretty = true;
		str.config.indent = true;
		try (JSONObjectOutput obj = str.makeObject()) {
			obj.writeBoolean("a", true);
			obj.writeChar("b", '!');
			obj.writeInt("c", 500);
			obj.writeDouble("d", 987654321.123456789);
			obj.writeString("foo", "bar");
			obj.writeObject("dfg", Arrays.asList("m","n","o",5.0f));
			obj.writeObject("obj", new JSONExternalizable(){
				@Override
				public void readJSON(JSONInput in) {
				}
				@Override
				public void writeJSON(JSONOutput out) {
					JSONObjectOutput objOut = out.makeObject();
					objOut.writeString("hello", "world");
				}});
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				str.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(w.toString());
	}

}
