package finchtemplate.util.hawk

sealed trait Algorithm

case object Sha256 extends Algorithm
