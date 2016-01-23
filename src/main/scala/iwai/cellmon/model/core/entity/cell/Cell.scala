package iwai.cellmon.model.core.entity.cell

import iwai.cellmon.model.core.entity.cell.{GsmCellLocation, NoCellLocation}

sealed trait Cell

sealed trait CellLocation extends Cell

sealed trait NoCell {
  self : Cell =>
}

final case class GsmCellLocation(lac: Int, cid: Int, psc: Int) extends CellLocation

final case class CdmaCellLocation(stationId: Int, systemId: Int, networkId: Int, stationLat: Int, stationLng: Int) extends CellLocation

case object NoCellLocation extends CellLocation with NoCell

@deprecated
final case class CellIdentities(identities: Set[CellIdentity]) extends Cell {
  def get = identities
}

sealed trait CellIdentity extends Cell

final case class GsmCellIdentity(mcc: Int, mnc: Int, lac: Int, cid: Int/*, neighbors: List[GsmCellIdentity]*/) extends CellIdentity

final case class WcdmaCellIdentity(mcc: Int, mnc: Int, lac: Int, cid: Int, psc: Int/*, neighbors: List[WcdmaCellIdentity]*/) extends CellIdentity

final case class LteCellIdentity(mcc: Int, mnc: Int, ci: Int, pci: Int, tac: Int/*, neighbors: List[LteCellIdentity]*/) extends CellIdentity

final case class CdmaCellIdentity(stationId: Int, systemId: Int, networkId: Int, lat: Int, lng: Int/*, neighbors: List[CdmaCellIdentity]*/) extends CellIdentity

case object NoCellIdentity extends CellIdentity with NoCell

case object Cell {
  def apply(l: android.telephony.CellLocation): Cell = CellLocation(l)

  //  def apply(i: List[android.telephony.CellInfo]): Cell = CellIdentities(i)
  def apply(i: List[android.telephony.CellInfo]): Cell = CellIdentity(i)

  //  trait JsonProtocol extends DefaultJsonProtocol with CellLocation.JsonProtocol with CellIdentities.JsonProtocol {
  //
  //    implicit object CellFormat extends RootJsonFormat[Cell] {
  //      override def write(cell: Cell): JsValue = cell match {
  //        case loc: CellLocation => loc.toJson(CellLocationFormat)
  //        case ids: CellIdentities => ids.toJson(CellIdentitiesFormat)
  //      }
  //
  //      private val cellReaders = List[JsonReader[Cell]](
  //        CellIdentitiesFormat.asInstanceOf[JsonReader[Cell]],
  //        CellLocationFormat.asInstanceOf[JsonReader[Cell]]
  //      )
  //
  //      override def read(value: JsValue): Cell =
  //        value.convertTo(walkingReader(cellReaders, new DeserializationException("Could not read Cell value")))
  //    }
  //
  //  }
  //
  //  object JsonProtocol extends JsonProtocol

}

case object CellLocation {
  def apply(l: android.telephony.CellLocation): CellLocation = l match {
    case gsm: android.telephony.gsm.GsmCellLocation => GsmCellLocation(gsm)
    case cdma: android.telephony.cdma.CdmaCellLocation => CdmaCellLocation(cdma)
  }

  //  trait JsonProtocol extends DefaultJsonProtocol with GsmCellLocation.JsonProtocol with CdmaCellLocation.JsonProtocol {
  //
  //    implicit object CellLocationFormat extends RootJsonFormat[CellLocation] {
  //      override def write(loc: CellLocation): JsValue = loc match {
  //        case gsm: GsmCellLocation => gsm.toJson(gsmCellLocationFormat)
  //        case cdma: CdmaCellLocation => cdma.toJson(cdmaCellLocationFormat)
  //      }
  //
  //      override def read(value: JsValue): CellLocation = (value.convertTo(safeReader(gsmCellLocationFormat)), value.convertTo(safeReader(cdmaCellLocationFormat))) match {
  //        case (Right(gsm), _: Left[_, _]) => gsm
  //        case (_: Left[_, _], Right(cdma)) => cdma
  //        case (_: Right[_, _], _: Right[_, _]) => deserializationError("Ambiguous CellLocation value: can be read as both, GsmCellLocation and CdmaCellLocation, values")
  //        case (Left(ea), Left(eb)) => deserializationError("Could not read CellLocation value:\n" + ea + "---------- and ----------\n" + eb)
  //      }
  //    }
  //
  //  }
  //
  //  object JsonProtocol extends JsonProtocol

}

case object GsmCellLocation {
  def apply(loc: android.telephony.gsm.GsmCellLocation): CellLocation =
  if (loc.getLac == -1 || loc.getCid == -1) NoCellLocation
  else apply(loc.getLac, loc.getCid, loc.getPsc)

  //  trait JsonProtocol extends DefaultJsonProtocol {
  //    implicit val gsmCellLocationFormat = jsonFormat(GsmCellLocation.apply, "lac", "cid", "psc")
  //  }
  //
  //  object JsonProtocol extends JsonProtocol

}

case object CdmaCellLocation {
  def apply(loc: android.telephony.cdma.CdmaCellLocation): CellLocation =
    if (loc.getBaseStationId == -1 || loc.getSystemId == -1 || loc.getNetworkId == -1) NoCellLocation
    else apply(loc.getBaseStationId, loc.getSystemId, loc.getNetworkId, loc.getBaseStationLatitude, loc.getBaseStationLongitude)

  //  trait JsonProtocol extends DefaultJsonProtocol {
  //    implicit val cdmaCellLocationFormat = jsonFormat(CdmaCellLocation.apply, "stationId", "systemId", "networkId", "stationLat", "stationLng")
  //  }
  //
  //  object JsonProtocol extends JsonProtocol

}

@deprecated
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

  //  trait JsonProtocol extends DefaultJsonProtocol with CellIdentity.JsonProtocol {
  //
  //    implicit object CellIdentitiesFormat extends RootJsonFormat[CellIdentities] {
  //      override def write(ids: CellIdentities): JsValue = ids.get.toJson(immSetFormat(CellIdentityFormat))
  //
  //      override def read(value: JsValue): CellIdentities = CellIdentities(value.convertTo[Set[CellIdentity]])
  //    }
  //
  //  }
  //
  //  object JsonProtocol extends JsonProtocol

}

case object CellIdentity {

  def apply(list: List[android.telephony.CellInfo]): CellIdentity =
    Option(list) match {
      case None => NoCellIdentity
      case Some(cellinfos) => {
        cellinfos.filter(_.isRegistered).headOption match {
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

  //  trait JsonProtocol extends DefaultJsonProtocol with GsmCellIdentity.JsonProtocol with WcdmaCellIdentity.JsonProtocol with LteCellIdentity.JsonProtocol with CdmaCellIdentity.JsonProtocol {
  //
  //    implicit object CellIdentityFormat extends RootJsonFormat[CellIdentity] {
  //      override def write(id: CellIdentity): JsValue = id match {
  //        case lte: LteCellIdentity => lte.toJson(lteCellIdentityFormat)
  //        case wcdma: WcdmaCellIdentity => wcdma.toJson(wcdmaCellIdentityFormat)
  //        case gsm: GsmCellIdentity => gsm.toJson(gsmCellIdentityFormat)
  //        case cdma: CdmaCellIdentity => cdma.toJson(cdmaCellIdentityFormat)
  //      }
  //
  //      private val cellIdentityReaders: List[JsonReader[CellIdentity]] = List(
  //        lteCellIdentityFormat.asInstanceOf[JsonReader[CellIdentity]],
  //        wcdmaCellIdentityFormat.asInstanceOf[JsonReader[CellIdentity]],
  //        gsmCellIdentityFormat.asInstanceOf[JsonReader[CellIdentity]],
  //        cdmaCellIdentityFormat.asInstanceOf[JsonReader[CellIdentity]]
  //      )
  //
  //      override def read(value: JsValue): CellIdentity =
  //        value.convertTo(walkingReader(cellIdentityReaders, new DeserializationException("Could not read CellIdentity value")))
  //    }
  //
  //    def walkingReader[A](readers: Seq[JsonReader[A]], failure: Exception) = new JsonReader[A] {
  //      def read(json: JsValue) = readers.foldLeft[Either[Exception, A]](Left(failure)) { (either, reader) =>
  //        either match {
  //          case Right(a) => Right(a)
  //          case Left(e) => json.convertTo(safeReader(reader)) match {
  //            case Right(a) => Right(a)
  //            case Left(_) => Left(e)
  //          }
  //        }
  //      } match {
  //        case Right(a) => a
  //        case Left(e) => throw e
  //      }
  //    }
  //
  //
  //  }
  //
  //  object JsonProtocol extends JsonProtocol

}


case object GsmCellIdentity {
  def apply(id: android.telephony.CellIdentityGsm/*, neighbors: List[GsmCellIdentity] = Nil*/): GsmCellIdentity =
    apply(id.getMcc, id.getMnc, id.getLac, id.getCid/*, neighbors*/)

  //  trait JsonProtocol extends DefaultJsonProtocol {
  //    implicit val gsmCellIdentityFormat = jsonFormat(GsmCellIdentity.apply, "mcc", "mnc", "lac", "cid")
  //  }
  //
  //  object JsonProtocol extends JsonProtocol

}

case object WcdmaCellIdentity {
  def apply(id: android.telephony.CellIdentityWcdma/*, neighbors: List[WcdmaCellIdentity] = Nil*/): WcdmaCellIdentity =
    apply(id.getMcc, id.getMnc, id.getLac, id.getCid, id.getPsc/*, neighbors*/)

  //  trait JsonProtocol extends DefaultJsonProtocol {
  //    implicit val wcdmaCellIdentityFormat = jsonFormat(WcdmaCellIdentity.apply, "mcc", "mnc", "lac", "cid", "psc")
  //  }
  //
  //  object JsonProtocol extends JsonProtocol

}

case object LteCellIdentity {
  def apply(id: android.telephony.CellIdentityLte/*, neighbors: List[LteCellIdentity] = Nil*/): LteCellIdentity =
    apply(id.getMcc, id.getMnc, id.getCi, id.getPci, id.getTac/*, neighbors*/)


  //  trait JsonProtocol extends DefaultJsonProtocol {
  //    implicit val lteCellIdentityFormat = jsonFormat(LteCellIdentity.apply, "mcc", "mnc", "ci", "pci", "tac")
  //  }
  //
  //  object JsonProtocol extends JsonProtocol

}


case object CdmaCellIdentity {
  def apply(id: android.telephony.CellIdentityCdma/*, neighbors: List[CdmaCellIdentity] = Nil*/): CdmaCellIdentity =
    apply(id.getBasestationId, id.getSystemId, id.getNetworkId, id.getLatitude, id.getLongitude/*, neighbors*/)

  //  trait JsonProtocol extends DefaultJsonProtocol {
  //    implicit val cdmaCellIdentityFormat = jsonFormat(CdmaCellIdentity.apply, "stationId", "systemId", "networkId", "stationLat", "stationLng")
  //  }
  //
  //  object JsonProtocol extends JsonProtocol

}




