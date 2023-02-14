package Models;

import Enums.ObjectTypes;

public class LocalState {
    public int lowerHeadingBase;
    public int higherHeadingBase;
    public boolean specialSector;

    public boolean teleporterFired = false;
    public boolean teleporterStillNotAppear = true;
    public int teleporterHeading;
    public int tpReason=0; // 0 : belum ada teleporter
                          // 1 : makan enemy yang lebih kecil
                          // 2 : kabur
    public GameObject target = null;
    public int intention = 0; // 0 : tidak ada aksi
                              // 1 : makan makanan
                              // 2 : serang player lain
                              // 3 : menghindar
}
