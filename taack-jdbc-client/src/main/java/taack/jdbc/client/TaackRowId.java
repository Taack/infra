package taack.jdbc.client;

import com.google.common.primitives.Longs;

import java.sql.RowId;

public final class TaackRowId implements RowId {
    final Long id;

    TaackRowId(Long id) {
        this.id = id;
    }

    @Override
    public byte[] getBytes() {
        return Longs.toByteArray(id);
    }
}
