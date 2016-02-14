import android.Keys._

android.Plugin.androidBuild

platformTarget in Android := "android-23"

minSdkVersion in Android := "21"

name := "cellmon"

scalaVersion := "2.11.7"




run <<= run in Android



resolvers ++= Seq(
	Resolver.sonatypeRepo("releases"),
	Resolver.sonatypeRepo("snapshots"),
	"jcenter" at "http://jcenter.bintray.com"
)

scalacOptions ++= Seq("-deprecation", "-feature", "-Xlint")


libraryDependencies ++= Seq(
  aar("com.android.support" % "multidex" % "1.0.1"),
	aar("com.android.support" % "support-v4" % "23.1.1"),
	aar("com.android.support" % "appcompat-v7" % "23.1.1"),
	aar("com.android.support" % "recyclerview-v7" % "23.1.1"),
	aar("org.macroid" %% "macroid" % "2.0.0-20150427"),
	aar("com.fortysevendeg" %% "macroid-extras" % "0.2"),
	"io.spray" %% "spray-json" % "1.3.2",
	"com.github.fommil" %% "spray-json-shapeless" % "1.1.0",
	"org.scalaz" %% "scalaz-core" % "7.1.6",
	"org.scalaz" %% "scalaz-concurrent" % "7.1.6",
	"de.greenrobot" % "eventbus" % "2.4.0",
	"com.jakewharton.timber" % "timber" % "4.1.0",
	"com.amazonaws" % "aws-android-sdk-s3" % "2.2.12",
	"com.amazonaws" % "aws-android-sdk-core" % "2.2.12",
	"com.amazonaws" % "aws-android-sdk-cognito" % "2.2.12",
	"com.amazonaws" % "aws-android-sdk-mobileanalytics" % "2.2.12",
	"com.amazonaws" % "aws-android-sdk-lambda" % "2.2.12",
	"com.amazonaws" % "aws-android-sdk-sns" % "2.2.12",
	"com.facebook.android" % "facebook-android-sdk" % "4.10.0",
	aar("com.google.android.gms" % "play-services-base" % "8.4.0"),
	aar("com.google.android.gms" % "play-services-gcm" % "8.4.0"),
	aar("com.google.android.gms" % "play-services-maps" % "8.4.0"),
	aar("com.google.android.gms" % "play-services-location" % "8.4.0")
	//	"com.github.nscala-time" %% "nscala-time" % "1.4.0",
	// compilerPlugin("org.brianmckenna" %% "wartremover" % "0.10")
)

addCompilerPlugin("org.brianmckenna" %% "wartremover" % "0.10")

scalacOptions in(Compile, compile) ++=
		(dependencyClasspath in Compile).value.files.map("-P:wartremover:cp:" + _.toURI.toURL)

scalacOptions in(Compile, compile) ++= Seq(
	"-P:wartremover:traverser:macroid.warts.CheckUi"
)

// proguardScala in Android := true
// 
// proguardOptions in Android ++= Seq(
// 	"-ignorewarnings",
// 	"-keep class scala.Dynamic",
// 	"-keep class scala.concurrent.ExecutionContext",
// 	"-keep class scala.Option",
// 	"-keep class scala.Function1",
// 	"-keep class scala.PartialFunction",
// 	"-keep class scala.util.parsing.combinator.Parsers",
// 	"-dontwarn scala.collection.**",
// 	"-keepattributes Signature",
// 	"-keep class spray.json.*",
// 	"""-keepclassmembers class ** {
// 	     public void onEvent*(**);
// 	  }""",
// 	"-keep class iwai.cellmon.R"
// )

//proguardCache in Android := Seq.empty


// Activate proguard for Scala
proguardScala in Android := true

// Activate proguard for Android
useProguard in Android := true

// Set proguard options
proguardOptions in Android ++= Seq(
	"-ignorewarnings",
	"-keepattributes Signature",
	"-keepattributes InnerClasses",
	"-keep class scala.Dynamic",
	"-keep class com.android.support.**",
	"-keep class com.typesafe.**",
	//	"-keep class akka.**",
	"-dontwarn scala.collection.**", // required from Scala 2.11.4
	"-keep class scala.collection.immutable.StringLike { *; }",
	//	"-keepclasseswithmembers class * { public <init>(java.lang.String, akka.actor.ActorSystem$Settings, akka.event.EventStream, akka.actor.Scheduler, akka.actor.DynamicAccess); }",
	//	"-keepclasseswithmembers class * { public <init>(akka.actor.ExtendedActorSystem); }",
	"-keep class scala.collection.SeqLike { public protected *; }",
	"-keep class scala.concurrent.**",
	"-keep class scala.reflect.ScalaSignature.**",
	//  "-keep class akka.actor.LightArrayRevolverScheduler { *; }",
	//  "-keep class akka.actor.LocalActorRefProvider { *; }",
	//  "-keep class akka.actor.CreatorFunctionConsumer { *; }",
	//  "-keep class akka.actor.TypedCreatorFunctionConsumer { *; }",
	//  "-keep class akka.dispatch.BoundedDequeBasedMessageQueueSemantics { *; }",
	//  "-keep class akka.dispatch.UnboundedMessageQueueSemantics { *; }",
	//  "-keep class akka.dispatch.UnboundedDequeBasedMessageQueueSemantics { *; }",
	//  "-keep class akka.dispatch.DequeBasedMessageQueueSemantics { *; }",
	//  "-keep class akka.actor.LocalActorRefProvider$Guardian { *; }",
	//  "-keep class akka.actor.LocalActorRefProvider$SystemGuardian { *; }",
	//  "-keep class akka.dispatch.UnboundedMailbox { *; }",
	//  "-keep class akka.actor.DefaultSupervisorStrategy { *; }",
	//  "-keep class akka.event.Logging$LogExt { *; }",
	"-keep class macroid.** { *; }",
	"-keep class com.google.** { *; }",
	"-keep class spray.json.*",
	//"-keep class de.greenrobot.event.**",
	"""-keepclassmembers,includedescriptorclasses class ** {
    public void onEvent*(**);
  }""",
	"-keep class * extends java.util.ListResourceBundle { protected java.lang.Object[][] getContents(); }",
	"-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable { public static final *** NULL; }",
	"-keepnames @com.google.android.gms.common.annotation.KeepName class *",
	"-keepclassmembernames class * { @com.google.android.gms.common.annotation.KeepName *; }",
	"-dontnote android.net.http.**",
	"-dontnote org.apache.http.**",
	"-dontwarn org.slf4j.**",
	"-dontwarn scalaz.concurrent.StrategysLow*",
	"-dontwarn com.fortysevendeg.macroid.extras.**",
	"-dontnote com.google.android.gms.maps.internal.zzad",
	"-dontnote macroid.**",
	"-dontnote scala.**",
	"-keepnames class * implements android.os.Parcelable { public static final ** CREATOR; }"
)

proguardCache in Android ++= Seq(
	"android.support",
	"scalaz",
	"macroid",
	"scala.reflect",
	"scala.xml",
	"scala.util.parsing.combinator",
	"scala.util.parsing.input",
	"scala.util.parsing.json"
)

dexMulti in Android := true

dexMainClasses in Android := Seq(
  "iwai/cellmon/LoggingApplication.class",
  "android/support/multidex/BuildConfig.class",
  "android/support/multidex/MultiDex$V14.class",
  "android/support/multidex/MultiDex$V19.class",
  "android/support/multidex/MultiDex$V4.class",
  "android/support/multidex/MultiDex.class",
  "android/support/multidex/MultiDexApplication.class",
  "android/support/multidex/MultiDexExtractor$1.class",
  "android/support/multidex/MultiDexExtractor.class",
  "android/support/multidex/ZipUtil$CentralDirectory.class",
  "android/support/multidex/ZipUtil.class"
)

// dexMinimizeMain in Android := true

// Exclude duplicates
packagingOptions in Android := PackagingOptions(excludes = Seq(
	"META-INF/DEPENDENCIES.txt",
	"META-INF/LICENSE.txt",
	"META-INF/NOTICE.txt",
	"META-INF/NOTICE",
	"META-INF/LICENSE",
	"META-INF/DEPENDENCIES",
	"META-INF/notice.txt",
	"META-INF/license.txt",
	"META-INF/dependencies.txt",
	"META-INF/LGPL2.1",
	"META-INF/services/com.fasterxml.jackson.databind.Module"
))
