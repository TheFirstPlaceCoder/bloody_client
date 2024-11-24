package com.client.utils.auth.records;

import com.client.utils.auth.enums.SubType;

public record User(SubType subType, boolean premium, String uid, String hwid, String name) {
}