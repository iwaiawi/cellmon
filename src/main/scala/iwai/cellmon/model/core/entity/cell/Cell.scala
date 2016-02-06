package iwai.cellmon.model.core.entity.cell

sealed trait Cell

sealed trait NoCell {
  self: Cell =>
}

sealed trait CellLocation extends Cell

final case class GsmCellLocation(lac: Int, cid: Int, psc: Int) extends CellLocation

final case class CdmaCellLocation(stationId: Int, systemId: Int, networkId: Int, stationLat: Int, stationLng: Int) extends CellLocation

case object NoCellLocation extends CellLocation with NoCell

@deprecated("Use CellIdentity instead.", "0.2")
final case class CellIdentities(identities: Set[CellIdentity]) extends Cell {
  def get = identities
}

sealed trait CellIdentity extends Cell

final case class GsmCellIdentity(mcc: Int, mnc: Int, lac: Int, cid: Int /*, neighbors: List[GsmCellIdentity]*/) extends CellIdentity

final case class WcdmaCellIdentity(mcc: Int, mnc: Int, lac: Int, cid: Int, psc: Int /*, neighbors: List[WcdmaCellIdentity]*/) extends CellIdentity

final case class LteCellIdentity(mcc: Int, mnc: Int, ci: Int, pci: Int, tac: Int /*, neighbors: List[LteCellIdentity]*/) extends CellIdentity

final case class CdmaCellIdentity(stationId: Int, systemId: Int, networkId: Int, lat: Int, lng: Int /*, neighbors: List[CdmaCellIdentity]*/) extends CellIdentity

case object NoCellIdentity extends CellIdentity with NoCell

case object Cell {
  def apply(l: android.telephony.CellLocation): Cell = CellLocation(l)

  def apply(i: List[android.telephony.CellInfo]): Cell = CellIdentity(i)
}

case object CellLocation {
  def apply(l: android.telephony.CellLocation): CellLocation = l match {
    case gsm: android.telephony.gsm.GsmCellLocation => GsmCellLocation(gsm)
    case cdma: android.telephony.cdma.CdmaCellLocation => CdmaCellLocation(cdma)
  }

}

case object GsmCellLocation {
  def apply(loc: android.telephony.gsm.GsmCellLocation): CellLocation =
    if (loc.getLac == -1 || loc.getCid == -1) NoCellLocation
    else apply(loc.getLac, loc.getCid, loc.getPsc)

}

case object CdmaCellLocation {
  def apply(loc: android.telephony.cdma.CdmaCellLocation): CellLocation =
    if (loc.getBaseStationId == -1 || loc.getSystemId == -1 || loc.getNetworkId == -1) NoCellLocation
    else apply(loc.getBaseStationId, loc.getSystemId, loc.getNetworkId, loc.getBaseStationLatitude, loc.getBaseStationLongitude)

}

@deprecated("Use CellIdentity instead.", "0.2")
case object CellIdentities {
  def apply(i: List[android.telephony.CellInfo]): CellIdentities =
    apply(
      (Option(i).map[List[CellIdentity]] {
        _.filter(_.isRegistered).collect {
          case gsm: android.telephony.CellInfoGsm => GsmCellIdentity(gsm.getCellIdentity)
          case wcdma: android.telephony.CellInfoWcdma => WcdmaCellIdentity(wcdma.getCellIdentity)
          case lte: android.telephony.CellInfoLte => LteCellIdentity(lte.getCellIdentity)
          case cdma: android.telephony.CellInfoCdma => CdmaCellIdentity(cdma.getCellIdentity)
        }
      }.filter(_.nonEmpty) match {
        case None => Nil
        case Some(ids) => ids
      }).toSet
    )

}

case object CellIdentity {

  def apply(list: List[android.telephony.CellInfo]): CellIdentity =
    Option(list) match {
      case None => NoCellIdentity
      case Some(cellInfos) => {
        cellInfos.find(_.isRegistered) match {
          case None => NoCellIdentity
          case Some(registered) => cellInfo2CellIdentity(registered)
        }
      }
    }

  def cellInfo2CellIdentity(registered: android.telephony.CellInfo): CellIdentity = {
    registered match {
      case gsm: android.telephony.CellInfoGsm => GsmCellIdentity(gsm.getCellIdentity)
      case wcdma: android.telephony.CellInfoWcdma => WcdmaCellIdentity(wcdma.getCellIdentity)
      case lte: android.telephony.CellInfoLte => LteCellIdentity(lte.getCellIdentity)
      case cdma: android.telephony.CellInfoCdma => CdmaCellIdentity(cdma.getCellIdentity)
      case _ => NoCellIdentity
    }
  }

}


case object GsmCellIdentity {
  def apply(id: android.telephony.CellIdentityGsm): CellIdentity =
    if (id.getMcc <= 0 || id.getMnc <= 0 || id.getCid == -1) NoCellIdentity
    else apply(id.getMcc, id.getMnc, id.getLac, id.getCid)

}

case object WcdmaCellIdentity {
  def apply(id: android.telephony.CellIdentityWcdma): CellIdentity =
    if (id.getMcc <= 0 || id.getMnc <= 0 || id.getCid == -1) NoCellIdentity
    else apply(id.getMcc, id.getMnc, id.getLac, id.getCid, id.getPsc)

}

case object LteCellIdentity {
  def apply(id: android.telephony.CellIdentityLte): CellIdentity =
    if (id.getMcc <= 0 || id.getMnc <= 0 || id.getCi == -1) NoCellIdentity
    else apply(id.getMcc, id.getMnc, id.getCi, id.getPci, id.getTac)

}

case object CdmaCellIdentity {
  def apply(id: android.telephony.CellIdentityCdma): CellIdentity =
    if (id.getSystemId <= 0 || id.getNetworkId <= 0 || id.getBasestationId == -1) NoCellIdentity
    else apply(id.getBasestationId, id.getSystemId, id.getNetworkId, id.getLatitude, id.getLongitude)

}




