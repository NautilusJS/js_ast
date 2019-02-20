package com.mindlin.jsast.json.api;

public interface JSONExternalizable {
	void readJSON(JSONInput in);
	void writeJSON(JSONOutput out);
}
