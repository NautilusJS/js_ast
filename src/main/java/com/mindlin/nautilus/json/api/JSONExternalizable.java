package com.mindlin.nautilus.json.api;

public interface JSONExternalizable {
	void readJSON(JSONInput in);
	void writeJSON(JSONOutput out);
}
