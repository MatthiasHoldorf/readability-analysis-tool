# Readability Analysis Tool (RAT) – Documentation

![rat-logo](/01_docbase/01_doc-images/rat-logo.jpg)

# Table of content
- [About](#id-section01)
 - [Analysis](#id-analysis)
 - [Report](#id-report)
- [Getting Started](#id-section02)
 - [Download](#id-download)
 - [Execution](#id-execution)
 - [Assembly Folder Structure](#id-assembly-folder-structure)
 - [Arguments](#id-arguments)
- [Readability Formulas](#id-section1)
- [Readability Statistics](#id-section2)
- [Readability Anomalies](#id-section3)
 - [Implemented Anomalies](#id-implemented-rules)
 - [Example Implementation](#id-example-implementation)
 - [Default Configuration](#id-default-configruation)
- [Configuration & Quality Gate](#id-section4)

<div id='id-section01'/>
# About

RAT is a tool to detect readability anomalies in text based on readability rules. Readability anomalies describe findings in a text which are difficult to read. The principle is similar to bug pattern in static code analysis. 

Name | Description
------------ | -------------
Category | Readability-Checker, Style-Checker
Name | Readability Analysis Tool (RAT)
Supported File Types | .docx
Supported Readability Rules | German
Features | <ul><li>Annotation of Readability Anomalies</li><li>Statistic Report</li><li>Configurable Anomaly Rules</li><li>Configurable Quality Gate</li><li>Automated detection of False Positives and Incorporated Anomalies (allowing semi-automated evaluation)</li></ul>
Performance | 40 Seconds for 10.000 words (50 pages)
Technologies | <ul><li>Java</li><li>UIMA</li><li>DKPro Core</li></ul>
License | GPLv3

<div id='id-analysis'/>
## Analysis

During an analysis a .docx file is enriched with comments (the readability anomaly findings). The .docx file is then saved as a new file with a "-rat.docx" suffix. This ensures that the original document cannot be corrupted by RAT. In case a document is analysed that already has a "-rat.docx" suffix, the **very same** document is altered.

The folder structure of an analysis might look like this:

![analysis-structure](/01_docbase/01_doc-images/analysis-structure.PNG)


The results of an analysis in a .docx file:


![results-docx](/01_docbase/01_doc-images/results-docx.PNG)

<div id='id-report'/>
## Report

Additionaly, RAT computes a report about statistics of the text (e.g., average words per sentence, reading time, most used nouns) and [readability formulas](https://en.wikipedia.org/wiki/Readability#Popular_readability_formulas). The report is stored in an HTML file next to the analyzed document.

For both the analysed document and the statstic report an optional outputDirectorycan can be specified via the argument "-o" or "--outputDirectory".

```
java -jar lib/rat-executor-cmd-0.5-SNAPSHOT.jar -o examples/output-directory/ examples/multiple-files/*
```

The HTML report aggregates all readability mesaurements and assess the overall readability of the text through a quality gate; similar to static code analysis tools.

Further, the report shows anomalies that were marked as declined (false positives) or incorporated by the user. RAT saves these information also in the XML of the .docx file itself. In this way, one can comprehend what action where taken during the editing of the document.

<div id='id-section02'/>
# Getting Started

<div id='id-download'/>
## Download RAT
You can get the current release of RAT [here](https://github.com/MatthiasHoldorf/rat-readability-analysis-tool/releases). Just unzip the file rat-{version}-tarball.tar and start your analysis.

<div id='id-execution'/>
## Execution
RAT is invoked via the command line. Examples to invoke RAT:

```
java -jar lib/rat-executor-cmd-0.5-SNAPSHOT.jar -o examples/output-directory/ examples/multiple-files/*
```
```
java -jar ../../lib/rat-executor-cmd-0.5-SNAPSHOT.jar --configurationPath ../../config/rat-config.xml *
```
```
java -jar ../../lib/rat-executor-cmd-0.5-SNAPSHOT.jar -c ../../config/rat-config.xml *.docx
```
```
java -jar ../../lib/rat-executor-cmd-0.5-SNAPSHOT.jar --configurationPath ../../config/rat-config.xml 45-page-9500-words-assignment.docx
```

<div id='id-assembly-folder-structure'/>
## Assembly Folder Structure

The assembly is delivered with example exatuables and documents. By that, the handling of the software can be learned quickly. 

<pre>
.
|---config
    |   rat-config.xml
|---examples
    |---config-in-folder
        |   45-page-9500-words-assignment.docx
        |   rat-config.xml
        |   rat-example.cmd
    |---multiple-files
        |   45-page-9500-words-assignment-0.docx
        |   45-page-9500-words-assignment-1.docx
        |   45-page-9500-words-assignment-2.docx
        |   rat-example.cmd
    |---output-directory
    |---single-file
        |   45-page-9500-words-assignment.docx
        |   rat-example.cmd
|---lib
    |   # jar files of the application
|   rat.cmd
|   rat.sh
|   rat.example.cmd
|   rat.example.sh
</pre>

<div id='id-arguments'/>
## Arguments

RAT can be invoked with the following optional arguments:

```
usage: Rat v0.4
 -c,--configurationPath <arg>   the file path of the configuration
 -o,--outputDirectory <arg>     the output directory for the document and statistic report
 -h,--help                      display help menu
```

The last argument must be a valid path to a potential file for an analysis. Command line wildcards can be used, e.g. `/*.docx`.

<div id='id-section1'/>
# Readability Formulas

RAT computes the below listed readability formulas. 

The results are saved in the same directory as the original document appended by a "rat-report.html" suffix, e.g. "{filename}-rat-report.html".

Readability Formula | Link
------------ | -------------
Flesch | https://de.wikipedia.org/wiki/Lesbarkeitsindex#Flesch-Reading-Ease
Wiener Sachtextformel | https://de.wikipedia.org/wiki/Lesbarkeitsindex#Wiener_Sachtextformel

<div id='id-section2'/>
# Readability Statistics

RAT computes the below listed readability statistics. 

The results are saved in the same directory as the original document with a "rat-report.html" suffix, e.g. "{filename}-rat-report.html".

 # | Name of statistic | Description
------------ | -------------  | ------------- 
1	| Reading Time | Based on 255 words per minute
2	| Speaking Time | Based on 125 words per minute
3	| Number of Sentences | 
4	| Number of Words | 
5	| Number of Syllables | 
6	| Number of Characters | 
7	| Average Number of Words per Sentence | 
8	| Average Number of Syllables per Sentence | 
9	| Average Number of Characters per Sentence | 
10	| Average Number of Syllables per Word | 
11	| Average Number of Characters per Word | 
12	| Most used Nouns | 
13	| Most used Verbs | 
14	| Most used Adjectives | 
15	| Most used Conjunctions | 
16	| Percentage of Nouns in Text | 
17	| Percentage of Verbs in Text | 
18	| Percentage of Adjectives in Text | 
19	| Percentage of Conjunctions in Text | 
20	| Percentage of specified keywords in configuration file | 

The statistics are presented in the HTML report:

![rat-statistics](/01_docbase/01_doc-images/rat-statistics.PNG)

Additionaly, RAT detects the occurence of words specified in the keywords section of the configuration file:

```xml
    <keywords>
        <keyword>QAware</keyword>
        <keyword>RAT</keyword>
    </keywords>
```
These are listed below the fixed computed statistics:

![rat-keywords](/01_docbase/01_doc-images/rat-keywords.PNG)

<div id='id-section3'/>
# Readability Anomalies

RAT detects the below listed readability anomalies and annotates them as comments in .docx files.

The annotated .docx file is saved in the same directory as the original document with a "-rat.docx" suffix, e.g. "{filename}-rat.{file-extension}".

<div id='id-implemented-rules'/>
## Implemented Readability Anomalies
- [AdjectiveStyle](#AdjectiveStyle)          
- [AmbiguousAdjectivesAndAdverbs](#AmbiguousAdjectivesAndAdverbs)
- [ConsecutiveFillers](#ConsecutiveFillers)
- [ConsecutivePrepositions](#ConsecutivePrepositions)
- [DoubleNegative](#DoubleNegative)
- [Filler](#Filler)
- [FillerSentence](#FillerSentence)
- [IndirectSpeech](#IndirectSpeech)
- [LeadingAttributes](#LeadingAttributes)
- [LongSentence](#LongSentence)
- [LongWord](#LongWord)
- [ModalVerb](#ModalVerb)
- [ModalVerbSentence](#ModalVerbSentence)
- [NestedSentence](#NestedSentence)
- [NestedSentenceConjunction](#NestedSentenceConjunction)
- [NestedSentenceDelimiter](#NestedSentenceDelimiter)
- [NominalStyle](#NominalStyle)
- [PassiveVoice](#PassiveVoice)
- [SentencesStartWithSameWord](#SentencesStartWithSameWord)
- [SubjectiveLanguage](#SubjectiveLanguage)
- [Superlatives](#Superlatives)
- [UnnecessarySyllables](#UnnecessarySyllables)

<div id='AdjectiveStyle'/>
## AdjectiveStyle

Attribute | Description
------------ | -------------
Anomaly Name | AdjectiveStyle
Description | Adjectives should only be used if they differentiate. If an adjective is not necessary, it should be omitted.
Severity | Major
Enabled | True
Entity | Part-of-speech
Threshold | 5 matches in one sentence (configurable)
Negative Example | Die *__schwergewichtigen__*  Prozessmodelle sind durch eine *__detaillierte__* Dokumentation gekennzeichnet, wodurch *__spätere__*  Änderungen an *__vorher definierten__*  Anforderungen nur mit *__hohem__*  Aufwand *__möglich__* sind.
 | [...] alltägliche Gewohnheiten.
Positive Example | Die Prozessmodelle sind *__detailliert__* dokumentiert. Anforderungen lassen sich lediglich mit einem *__hohen__* Aufwand ändern.
 | [...] <s>alltägliche</s> Gewohnheiten.

<div id='AmbiguousAdjectivesAndAdverbs'/>
## AmbiguousAdjectivesAndAdverbs

Attribute | Description
------------ | -------------
Anomaly Name | AmbiguousAdjectivesAndAdverbs
Description | Ambiguous Adverbs and Adjectives describe imprecise words.
Severity | Minor
Enabled | True
Entity | Word
Threshold | 1 match in the text (not configurable)
Word List | annäherend, beinahe, gewöhnlich, maximal, minimal, möglicherweise, nahezu, optimal, vielleicht, ziemlich
Negative Examples | Die Programmierer sind in Extreme Programming in *__nahezu__* allen Techniken und Vorgängen integriert.
 | Der Projektleiter muss folglich *__maximal__* in das Projekt integriert sein.
Positive Examples | Die Programmierer sind in Extreme Programming in allen Techniken und Vorgängen integriert, außer [...] 
 | Der Projektleiter muss folglich zwei Arbeitstage in der Woche am Projekt arbeiten. 

<div id='ConsecutiveFillers'/>
## ConsecutiveFillers
 
Attribute | Description
------------ | -------------
Anomaly Name | ConsecutiveFillers
Description | Two consecutive fillers inflate a sentence.
Severity | Minor
Enabled | True
Entity | Word
Threshold | 2 consecutive matches in the text (not configurable)
Word List | aber, abermals, allein, allemal, allem Anschein, nach, allenfalls, allenthalben, allerdings, allesamt, allzu, also, andauernd, andererseits, andernfalls, anscheinend, an sich, auch, auffallend, aufs Neue, augenscheinlich, ausdrücklich, ausgerechnet, ausnahmslos, außerdem, äußerst, beinahe, bei weitem, bekanntlich, bereits, besonders, bestenfalls, bestimmt, bloß, dabei, dafür, dagegen, daher, damals, danach, dann und wann, demgegenüber, demgemäß, demnach, denkbar, denn, dennoch, deshalb, des Öfteren, des ungeachtet, deswegen, doch, durchaus, durchweg, eben, eigentlich, ein bisschen, einerseits, einfach, einige, einigermaßen, einmal, ein wenig, ergo, erheblich, etliche, etwa, etwas, fast, folgendermaßen, folglich, förmlich, fortwährend, fraglos, freilich, ganz, ganz und gar, gänzlich, gar, gelegentlich, gemeinhin, genau, geradezu, gewiss, gewissermaßen, glatt, gleichsam, gleichwohl, glücklicherweise, gottseidank, größtenteils, halt, häufig, hingegen, hinlänglich, höchst, höchstens, im Allgemeinen, immer, immerhin, immerzu, in der Tat, indessen, in diesem Zusammenhang, infolgedessen, insbesondere, inzwischen, irgend, irgendein, irgendjemand, irgendwann, irgendwie, irgendwo, ja, je, jedenfalls, jedoch, jemals, kaum, keinesfalls, keineswegs, längst, lediglich, leider, letztlich, manchmal, mehr oder weniger, meines Erachtens, meinetwegen, meist, meistens, meistenteils, mindestens, mithin, mitunter, möglicherweise, möglichst, nämlich, naturgemäß, natürlich, neuerdings, neuerlich, neulich, nichtsdestoweniger, nie, niemals, nun, nur, offenbar, offenkundig, offensichtlich, oft, ohnedies, ohne weiteres, ohne Zweifel, partout, plötzlich, praktisch, quasi, recht, reichlich, reiflich, relativ, restlos, richtiggehend, rundheraus, rundum, samt und sonders, sattsam, schlicht, schlichtweg, schließlich, schlussendlich, schon, sehr, selbst, selbstredend, selbstverständlich, selten, seltsamerweise, sicher, sicherlich, so, sogar, sonst, sowieso, sowohl als auch, sozusagen, stellenweise, stets, trotzdem, überaus, überdies, überhaupt, übrigens, umständehalber, unbedingt, unerhört, ungefähr, ungemein, ungewöhnlich, ungleich, unglücklicherweise, unlängst, unsagbar, unsäglich, unstreitig, unzweifelhaft, vermutlich, vielfach, vielleicht, voll, vollends, völlig, vollkommen, voll und ganz, von neuem, wahrscheinlich, weidlich, weitgehend, wenigstens, wieder, wiederum, wirklich, wohl, wohlgemerkt, womöglich, ziemlich, zudem, zugegeben, zumeist, zusehends, zuweilen, zweifellos, zweifelsfrei, zweifelsohne
Negative Example | Mit dem Entwicklungsfortschritt nimmt die Zahl der Tests *__folglich fortwährend__* zu.
 | In einem Projekt sollten *__daher stets__* alle dieser Techniken angewandt werden.
 | Letzteres ist allerdings nicht im Sinne des Kunden und *__sollte daher__* selten praktiziert werden.
 | Diese Art der Kommunikation und Planung funktioniert *__allerdings nur__* bis zu einem bestimmten Grad.
Positive Example | Mit dem Entwicklungsfortschritt nimmt die Zahl der Tests *__folglich <s>fortwährend</s>__* zu.
 | In einem Projekt sollten *__daher <s>stets</s>__* alle dieser Techniken angewandt werden.
 | Letzteres ist <s>allerdings</s> nicht im Sinne des Kunden und *__sollte <s>daher</s>__* selten praktiziert werden.
 | Diese Art der Kommunikation und Planung funktioniert *<s>__allerdings</s> nur__* bis zu einem bestimmten Grad.

<div id='ConsecutivePrepositions'/>
## ConsecutivePrepositions

Attribute | Description
------------ | -------------
Anomaly Name | ConsecutivePrepositions
Description | Two consecutive spatial expressions (prepositions) most likely irritate a reader.
Severity | Minor
Enabled | True
Entity | Word
Threshold | 2 consecutive matches in the text (not configurable)
Word List | auf, an, hinter, in, neben, über, unter, vor, zwischen
Negative Example | Wir geben nichts *__auf unter__* Druck zustande gekommene Verträge.
Positive Example | Wir geben nichts Verträge die unter Druck zustande gekommen sind.

<div id='DoubleNegative'/>
## DoubleNegative
 
Attribute | Description
------------ | -------------
Anomaly Name | DoubleNegative
Description | Double negation in a sentence makes it hard to capture its meaning.
Severity | Major
Enabled | True
Entity | Word
Threshold | 2 matches in one sentence (configurable)
Word List | ausnahmslos, außer, kein, keinerlei, keinesfalls, keineswegs, mitnichten, nein, nicht, nichtig, nichts, nie, niemals, niemand, nimmer, nirgends, nirgendwo, ohne, unterlegen, ablehnen, entfernen, ignorieren, verbieten, verbot, verbots, verbotes, vermeiden, weglassen
Negative Example | Das Schreiben von Tests ist zwar wichtig, sollte jedoch *__nicht__* verpflichtend für Funktionen sein, die *__nicht__* öffentlich sichtbar sind.
 | Eine erzwungene Verantwortung führt *__nicht__* zu diesem Effekt und ist daher *__nicht__* erwünscht.
Positive Example | Das Schreiben von Tests ist wichtig. Das Testen privater Methoden sollte nicht verpflichtend sein.
 | Eine erzwungene Verantwortung erzielt nicht den gewünschten Effekt.

<div id='Filler'/>
## Fillers

Attribute | Description
------------ | -------------
Anomaly Name | Fillers
Description | Unnecessary words, e.g. fillers, inflate a sentence.
Severity | Minor
Enabled | False
Entity | Word
Threshold | 1 match in the text (not configurable)
Word List | aber, abermals, allein, allemal, allem Anschein, nach, allenfalls, allenthalben, allerdings, allesamt, allzu, also, andauernd, andererseits, andernfalls, anscheinend, an sich, auch, auffallend, aufs Neue, augenscheinlich, ausdrücklich, ausgerechnet, ausnahmslos, außerdem, äußerst, beinahe, bei weitem, bekanntlich, bereits, besonders, bestenfalls, bestimmt, bloß, dabei, dafür, dagegen, daher, damals, danach, dann und wann, demgegenüber, demgemäß, demnach, denkbar, denn, dennoch, deshalb, des Öfteren, des ungeachtet, deswegen, doch, durchaus, durchweg, eben, eigentlich, ein bisschen, einerseits, einfach, einige, einigermaßen, einmal, ein wenig, ergo, erheblich, etliche, etwa, etwas, fast, folgendermaßen, folglich, förmlich, fortwährend, fraglos, freilich, ganz, ganz und gar, gänzlich, gar, gelegentlich, gemeinhin, genau, geradezu, gewiss, gewissermaßen, glatt, gleichsam, gleichwohl, glücklicherweise, gottseidank, größtenteils, halt, häufig, hingegen, hinlänglich, höchst, höchstens, im Allgemeinen, immer, immerhin, immerzu, in der Tat, indessen, in diesem Zusammenhang, infolgedessen, insbesondere, inzwischen, irgend, irgendein, irgendjemand, irgendwann, irgendwie, irgendwo, ja, je, jedenfalls, jedoch, jemals, kaum, keinesfalls, keineswegs, längst, lediglich, leider, letztlich, manchmal, mehr oder weniger, meines Erachtens, meinetwegen, meist, meistens, meistenteils, mindestens, mithin, mitunter, möglicherweise, möglichst, nämlich, naturgemäß, natürlich, neuerdings, neuerlich, neulich, nichtsdestoweniger, nie, niemals, nun, nur, offenbar, offenkundig, offensichtlich, oft, ohnedies, ohne weiteres, ohne Zweifel, partout, plötzlich, praktisch, quasi, recht, reichlich, reiflich, relativ, restlos, richtiggehend, rundheraus, rundum, samt und sonders, sattsam, schlicht, schlichtweg, schließlich, schlussendlich, schon, sehr, selbst, selbstredend, selbstverständlich, selten, seltsamerweise, sicher, sicherlich, so, sogar, sonst, sowieso, sowohl als auch, sozusagen, stellenweise, stets, trotzdem, überaus, überdies, überhaupt, übrigens, umständehalber, unbedingt, unerhört, ungefähr, ungemein, ungewöhnlich, ungleich, unglücklicherweise, unlängst, unsagbar, unsäglich, unstreitig, unzweifelhaft, vermutlich, vielfach, vielleicht, voll, vollends, völlig, vollkommen, voll und ganz, von neuem, wahrscheinlich, weidlich, weitgehend, wenigstens, wieder, wiederum, wirklich, wohl, wohlgemerkt, womöglich, ziemlich, zudem, zugegeben, zumeist, zusehends, zuweilen, zweifellos, zweifelsfrei, zweifelsohne
Negative Example | Mit dem Entwicklungsfortschritt nimmt die Zahl der Tests *__folglich fortwährend__* zu.
 | Diese Art der Kommunikation und Planung funktioniert *__allerdings nur__* bis zu einem bestimmten Grad.
Positive Example | Mit dem Entwicklungsfortschritt nimmt die Zahl der Tests *__folglich <s>fortwährend</s>__* zu.
 | Diese Art der Kommunikation und Planung funktioniert *<s>__allerdings</s> nur__* bis zu einem bestimmten Grad.

<div id='FillerSentence'/>
## FillersSentence
 
Attribute | Description
------------ | -------------
Anomaly Name | FillersSentence
Description | Many unnecessary words, e.g. fillers, inflate a sentence.
Severity | Major
Enabled | True
Entity | Word
Threshold | 3 matches in one sentence (configurable)
Word List | aber, abermals, allein, allemal, allem Anschein, nach, allenfalls, allenthalben, allerdings, allesamt, allzu, also, andauernd, andererseits, andernfalls, anscheinend, an sich, auch, auffallend, aufs Neue, augenscheinlich, ausdrücklich, ausgerechnet, ausnahmslos, außerdem, äußerst, beinahe, bei weitem, bekanntlich, bereits, besonders, bestenfalls, bestimmt, bloß, dabei, dafür, dagegen, daher, damals, danach, dann und wann, demgegenüber, demgemäß, demnach, denkbar, denn, dennoch, deshalb, des Öfteren, des ungeachtet, deswegen, doch, durchaus, durchweg, eben, eigentlich, ein bisschen, einerseits, einfach, einige, einigermaßen, einmal, ein wenig, ergo, erheblich, etliche, etwa, etwas, fast, folgendermaßen, folglich, förmlich, fortwährend, fraglos, freilich, ganz, ganz und gar, gänzlich, gar, gelegentlich, gemeinhin, genau, geradezu, gewiss, gewissermaßen, glatt, gleichsam, gleichwohl, glücklicherweise, gottseidank, größtenteils, halt, häufig, hingegen, hinlänglich, höchst, höchstens, im Allgemeinen, immer, immerhin, immerzu, in der Tat, indessen, in diesem Zusammenhang, infolgedessen, insbesondere, inzwischen, irgend, irgendein, irgendjemand, irgendwann, irgendwie, irgendwo, ja, je, jedenfalls, jedoch, jemals, kaum, keinesfalls, keineswegs, längst, lediglich, leider, letztlich, manchmal, mehr oder weniger, meines Erachtens, meinetwegen, meist, meistens, meistenteils, mindestens, mithin, mitunter, möglicherweise, möglichst, nämlich, naturgemäß, natürlich, neuerdings, neuerlich, neulich, nichtsdestoweniger, nie, niemals, nun, nur, offenbar, offenkundig, offensichtlich, oft, ohnedies, ohne weiteres, ohne Zweifel, partout, plötzlich, praktisch, quasi, recht, reichlich, reiflich, relativ, restlos, richtiggehend, rundheraus, rundum, samt und sonders, sattsam, schlicht, schlichtweg, schließlich, schlussendlich, schon, sehr, selbst, selbstredend, selbstverständlich, selten, seltsamerweise, sicher, sicherlich, so, sogar, sonst, sowieso, sowohl als auch, sozusagen, stellenweise, stets, trotzdem, überaus, überdies, überhaupt, übrigens, umständehalber, unbedingt, unerhört, ungefähr, ungemein, ungewöhnlich, ungleich, unglücklicherweise, unlängst, unsagbar, unsäglich, unstreitig, unzweifelhaft, vermutlich, vielfach, vielleicht, voll, vollends, völlig, vollkommen, voll und ganz, von neuem, wahrscheinlich, weidlich, weitgehend, wenigstens, wieder, wiederum, wirklich, wohl, wohlgemerkt, womöglich, ziemlich, zudem, zugegeben, zumeist, zusehends, zuweilen, zweifellos, zweifelsfrei, zweifelsohne
Negative Example | In einem Projekt sollten *__daher__* *__stets__* alle dieser Techniken angewandt werden, da *__andernfalls__* keine optimale Wirkung erzielt werden kann.
Positive Example | In einem Projekt sollten <s>*__daher__* *__stets__*</s> alle diese Techniken angewandt werden, da andernfalls keine Wirkung erzielt wird.

<div id='IndirectSpeech'/>
## IndirectSpeech
 
Attribute | Description
------------ | -------------
Anomaly Name | IndirectSpeech
Description | Indirect speech should be avoided, since it is not clear who the actor in the sentence is.
Severity | Minor
Enabled | False
Entity | Word
Threshold | 1 match in the text (not configurable)
Word List | man
Negative Example | Die Rollen im Extreme Programming können getauscht werden. Dadurch wird die Kreativität gefördert und *__man__* löst sich von Gewohnheiten.
Positive Example | Die Rollen im Extreme Programming können getauscht werden. Dadurch wird die Kreativität gefördert und *__die Teammitglieder__* lösen sich von Gewohnheiten.

<div id='LeadingAttributes'/>
## LeadingAttributes
 
Attribute | Description
------------ | -------------
Anomaly Name | LeadingAttributes
Description | If there are too many words between the article of a noun and the noun itself, the reader's short-term memory might forget the article.
Severity | Minor
Enabled | True
Entity | Word
Threshold | 4 words between article and noun (configurable)
Word List | man
Negative Example | Die Programmierung erfolgt in *__einem__* sowohl freien, als auch stark kontrollierten *__Stil__*.
 | *__Ein schleichender__*, von den Nutzern typischerweise durch Aussagen wie „Das ist so langsam“ oder „Die Zahlen taugen nichts“ kommunizierter *__Qualitätsverlust__*.
Positive Example | Die Programmierung erfolgt in *__einem Stil__*, der frei und zugleich stark kontrolliert ist.
 | *__Ein schleichender__* *__Qualitätsverlust__*, der von den Nutzern typischerweise durch Aussagen wie „Das ist so langsam“ oder „Die Zahlen taugen nichts“ kommunizierter wird.

<div id='LongSentence'/>
## LongSentence

Attribute | Description
------------ | -------------
Anomaly Name | LongSentence
Description | Long sentences tend to carry too much information and should be avoided. 
Severity | Critical
Enabled | True
Entity | Word
Threshold | 30 words in a sentence
Negative Example | Stellt das Team fest, dass es für die aktuelle Iteration nicht alle zuvor festgelegten Funktionen umsetzten kann, sollte mit der Geschäftsseite (speziell dem Kunden) eine Auswahl der Funktionen der aktuellen Storycards erfolgen, die für diese Iteration unbedingt erfüllt werden sollten.
Positive Example | Wenn in der aktuellen Iteration nicht alle Funktionen vom Team umgesetzt werden können, sollte der Kunde kontaktiert werden. Das Team und der Kunde treffen dann eine Auswahl der Funktionen die in der Iteration umgesetzt werden sollen.

<div id='LongWord'/>
## LongWord

Attribute | Description
------------ | -------------
Anomaly Name | LongWord
Description | Words with many syllables tend to be unfamiliar and harder to understand.
Severity | Critical
Enabled | True
Entity | Word
Threshold | 8 syllables (configurable)
Negative Example | In diesem Fall schließt sich eine *__Dokumentationserstellung__* an, die für zukünftige Veränderungen der Software einen leichten Einstieg ermöglichen.
Positive Example | In diesem Fall wird eine Dokumentation erstellt, die für zukünftige Veränderungen der Software einen leichten Einstieg ermöglicht.

<div id='ModalVerb'/>
## ModalVerb

Attribute | Description
------------ | -------------
Anomaly Name | ModalVerb
Description | With modal verbs, critical statements can be mitigated.
Severity | Minor
Entity | Word
Threshold | 1 (not configurable)
Enabled | False
Word List | dürfen, dürfte, dürften, können, könnte, könnten, möchte, möchten, mögen, müssen, müsste, müssten, sollen, sollte, sollten, wollen, wollte, wollten
Negative Example | Wir *__sollten__* das Produkt bis zum Ende des Jahres fertig entwickelt haben.
Positive Example | Wir *__werden__* das Produkt bis zum Ende des Jahres fertiggestellt haben.

<div id='ModalVerbSentence'/>
## ModalVerbSentence

Attribute | Description
------------ | -------------
Anomaly Name | ModalVerbSentence
Description | With modal verbs, critical statements can be mitigated. Too many modal verbs also inflate a sentence.
Severity | Minor
Entity | Word
Threshold | 2 matches in one sentence (configurable)
Enabled | True
Word List | dürfen, dürfte, dürften, können, könnte, könnten, möchte, möchten, mögen, müssen, müsste, müssten, sollen, sollte, sollten, wollen, wollte, wollten
Negative Example | Wir *__sollten__* das Produkt bis zum Ende des Jahres fertig entwickelt haben.
Positive Example | Wir *__werden__* das Produkt bis zum Ende des Jahres fertiggestellt haben.

<div id='NestedSentence'/>
## NestedSentence

Attribute | Description
------------ | -------------
Anomaly Name | NestedSentence
Description | Nested sentence constructions hinder a reader to quickly understand a sentence.
Severity | Critical
Entity | Part-of-speech
Threshold | 6 matches of conjunctions or delimiters in one sentence (configurable)
Enabled | True

<div id='NestedSentenceConjunction'/>
## NestedSentenceConjunction

Attribute | Description
------------ | -------------
Anomaly Name | NestedSentenceConjunction
Description | Nested sentence constructions hinder a reader to quickly understand a sentence.
Severity | Major
Entity | Part-of-speech
Threshold | 3 matches of conjunctions in one sentence (configurable)
Enabled | False

<div id='NestedSentenceDelimiter'/>
## NestedSentenceDelimiter

Attribute | Description
------------ | -------------
Anomaly Name | NestedSentenceDelimiter
Description | Nested sentence constructions hinder a reader to quickly understand a sentence.
Severity | Major
Entity | Token
Threshold | 3 matches of delimiters in one sentence (configurable)
Enabled | False

<div id='NominalStyle'/>
## NominalStyle

Attribute | Description
------------ | -------------
Anomaly Name | NominalStyle
Description | Too many abstract nouns in one sentence should be avoided.
Severity | Major
Entity | Word
Threshold | 3 matches in one sentence (configurable)
Enabled | True
Word List | heit, heiten, keit, keiten, ung, ungen (word ends with)
Negative Example | In der *__Software-Entwicklung__* dienen Prozessmodelle der *__Festlegung__* des Vorgehens und des Ablaufs zur *__Erstellung__* einer Software.
 | Diese fehlende Flexibilität führte zur *__Entwicklung__* von leichtgewichtigen, agilen Prozessmodellen, die von *__Änderungen__* der *__Anforderungen__* während des Projektes ausgehen.
Positive Example | In der *__Software-Entwicklung__* legen Prozessmodelle den Ablauf zur *__Erstellung__* von Software fest.
 | Durch die fehlende Flexibilität sind agile Prozessmodelle entstanden. Diese Modelle gehen von sich ändernden *__Anforderungen__* aus.

<div id='PassiveVoice'/>
## PassiveVoice

Attribute | Description
------------ | -------------
Anomaly Name | PassiveVoice
Description | Sentence in passive voice tend to be longer and hide the actor of a sentence.
Severity | Major
Entity | Word
Threshold | 1 match in a sentence (not configurable)
Enabled | False
Word List | wurde, wurden, wurdest, worde, worden, geworden
Negative Example | Das Projekt *__wurde__* mit einem schwergewichtigen Prozessmodell begonnen und drohte zu scheitern.
 | Die Software wird deployed, wenn eine Task einer Storycard erfüllt wurde.
Positive Example | Das Projekt begann mit einem schwergewichtigen Prozessmodell und drohte zu scheitern.
 | Die Software wird deployed, wenn ein Entwickler eine Task einer Storycard fertiggestellt.

<div id='SentencesStartWithSameWord'/>
## SentencesStartWithSameWord

Attribute | Description
------------ | -------------
Anomaly Name | SentencesStartWithSameWord
Description | Sentences that start with the same word are similar  to an enumeration and hinder the reading flow.
Severity | Minor
Entity | Word
Threshold | 2 matches in successive sentence (configurable)
Enabled | True

<div id='SubjectiveLanguage'/>
## SubjectiveLanguage

Attribute | Description
------------ | -------------
Anomaly Name | SubjectiveLanguage
Description | Words with semantics that are not objectively defined should be avoided. Such words leave room for misinterpretation.
Severity | Minor
Entity | Word
Threshold | 1 match in the text (not configurable)
Enabled | True
Word List | benutzerfreundlich, Benutzerfreundlichkeit, ein bisschen, einfach zu Nutzen, einfache Nutzbarkeit, kosteneffizient, kosteneffizienz, kostengünstig, logischerweise, preiswert, selbstverständlich, ziemlich
Negative Example | Wir *__sollten__* das Produkt bis zum Ende des Jahres fertig entwickelt haben.
Positive Example | Wir *__werden__* das Produkt bis zum Ende des Jahres fertiggestellt haben.

<div id='Superlatives'/>
## Superlatives

Attribute | Description
------------ | -------------
Anomaly Name | Superlatives
Description | Superlatives express a relation of an entity to all other entities. In general, this is difficult to prove.
Severity | Minor
Entity | Word
Threshold | 1 match in the text (not configurable)
Enabled | True
Word List | beste, höchste, super, unglaublich, wichtigste, wichtigsten
Negative Example | Die Programmierer nehmen die *__wichtigste__* Gruppe ein.
 | Die Benutzerfreundlichkeit der Software ist die *__beste__* auf dem Markt.

<div id='UnnecessarySyllables'/>
## UnnecessarySyllables

Attribute | Description
------------ | -------------
Anomaly Name | UnnecessarySyllables
Description | Words containing unnecessary syllables should be shortened.
Severity | Minor
Entity | Word
Threshold | 2 matches in one sentence (configurable)
Enabled | True
Word List | abmahnen, abstrafen, ansonsten, aufgabenstellung, aufgabenstellungen, ausleseverfahren, bedrohungssignal, bedrohungssignalen, einflussnahme, einkürzen, gefährdungspotential, gefährdungspotenzial, grundprinzip, heilungsverlauf, heutzutage, lernprozess, letztendlich, motivationsstruktur, motivationsstrukturen, neuartig, problemstellung, problemstellungen, rückantwort, rückantworten, rücksichtsnahme, stillschweigen , themenstellung, themenstellungen, überprüfen, wettergeschehen, witterungsbedinungen, zielsetzung, zielsetzungen, zukunftsporgnose
Negative Example | Für die Entwickler gilt es in der Vorbereitungsphase alle zur Verfügung stehenden Technologien einzubeziehen und sämtliche relevante Lösungsalternativen für die *__Problemstellung__* des Kunden prototypenhaft auszuarbeiten.
 | *__Letztendlich__* verdeutlichen die erläuterten Techniken die gegenseitige Abhängigkeit – mit Ausnahme des Testens, welches auch effektiv einzeln angewendet werden kann.
Positive Example | Für die Entwickler gilt es in der Vorbereitungsphase alle zur Verfügung stehenden Technologien einzubeziehen und sämtliche relevante Lösungsalternativen für die *__Probleme__* des Kunden prototypenhaft auszuarbeiten.
 | *__Letztlich__* verdeutlichen die erläuterten Techniken die gegenseitige Abhängigkeit – mit Ausnahme des Testens, welches auch effektiv einzeln angewendet werden kann.

<div id='id-example-implementation'/>
## Example Implementation

An example of a readability rule constitutes the following soure code, that detects consecutive fillers in a sentence:

```Java
@TypeCapability(inputs = { "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
        "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence" }, outputs = {
                "de.qaware.rat.type.RatReadabilityAnomaly" })
public class ConsecutiveFillersAnnotator extends JCasAnnotator_ImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsecutiveFillersAnnotator.class);

    public static final String SEVERITY = RuleParameter.SEVERITY;
    @ConfigurationParameter(name = RuleParameter.SEVERITY, mandatory = true, defaultValue = "Minor")
    protected String severity;

    @Override
    public void process(JCas aJCas) {
        LOGGER.info("Start ConsecutiveFillersAnnotator");

        try {
            String[] fillers = ImporterUtils.readWordlist("word-lists/Fillers.txt");
            List<Token> words = TextStatistic.getWordsInDocument(aJCas);

            int maxSize = words.size() - 1;
            for (int i = 0; i < words.size(); i++) {

                if (i + 1 <= maxSize) {
                    if (Arrays.asList(fillers).contains(words.get(i).getCoveredText().toLowerCase())
                            && Arrays.asList(fillers).contains(words.get(i + 1).getCoveredText().toLowerCase())) {
                        List<String> violations = new ArrayList<String>();
                        violations.add(words.get(i).getCoveredText());
                        violations.add(words.get(i + 1).getCoveredText());

                        UimaUtils.createRatReadabilityAnomaly(aJCas, "ConsecutiveFillers", "ReadabilityAnomaly",
                                severity,
                                "Vermeiden Sie aufeinanderfolgende Füllwörter. ("
                                        + CollectionUtils.printStringList(violations) + ")",
                                violations, words.get(i).getBegin(), words.get(i).getEnd());
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("The ConsecutiveFillersAnnotator failed.");
        }
    }
}
```

<div id='id-default-configruation'/>
## Default Configuration

The default readability rule configuration:

 # | Rule | Threshold | Enabled
 ------------ | ------------- | ------------- | -------------
1  | AdjectiveStyleAnnotator |	5 | true
2  | AmbiguousAdjectivesAndAdverbsAnnotator	| | true
3  | ConsecutiveFillersAnnotator | | true	
4  | ConsecutivePrepositionsAnnotator | | true	
5  | DoubleNegativeAnnotator |	2 | true
6  | Fillers | | false
7  | FillerSentenceAnnotator | 	3 | true
8  | IndirectSpeech | | false
9  | LeadingAttributesAnnotator | 4 | true
10 | LongSentenceAnnotator | 35 | true
11 | LongWordAnnotator | 8 | true
12 | ModalVerb | | false
13 | ModalVerbSentenceAnnotator |	2 | true
14 | NestedSentenceAnnotator | 6 | true
15 | NestedSentenceConjunction | 3 | false
16 | NestedSentenceDelimiter | 3 | false
17 | NominalStyleAnnotator|	3 | true
18 | PassiveVoiceAnnotator | | true
19 | SentencesStartWithSameWordAnnotator |	2 | true
20 | SubjectiveLanguageAnnotator | | true
21 | SuperlativeAnnotator | | true
22 | UnnecessarySyllablesAnnotator | | true


```xml
<anomalies>
    <anomaly-rule>
        <name>AdjectiveStyle</name>
        <severity>Major</severity>
        <threshold>6</threshold>
        <enabled>true</enabled>
    </anomaly-rule>
    <anomaly-rule>
        <name>AmbiguousAdjectivesAndAdverbs</name>
        <severity>Minor</severity>
        <enabled>true</enabled>
    </anomaly-rule>
    <anomaly-rule>
        <name>ConsecutiveFillers</name>
        <severity>Minor</severity>
        <enabled>true</enabled>
    </anomaly-rule>
    <anomaly-rule>
        <name>ConsecutivePrepositions</name>
        <severity>Minor</severity>
        <enabled>true</enabled>
    </anomaly-rule>
    <anomaly-rule>
        <name>DoubleNegative</name>
        <severity>Major</severity>
        <threshold>2</threshold>
        <enabled>true</enabled>
    </anomaly-rule>
    <anomaly-rule>
        <name>Filler</name>
        <severity>Minor</severity>
        <enabled>false</enabled>
    </anomaly-rule>
    <anomaly-rule>
        <name>FillerSentence</name>
        <severity>Major</severity>
        <threshold>3</threshold>
        <enabled>true</enabled>
    </anomaly-rule>
    <anomaly-rule>
        <name>IndirectSpeech</name>
        <severity>Minor</severity>
        <enabled>false</enabled>
    </anomaly-rule>
    <anomaly-rule>
        <name>LeadingAttributes</name>
        <severity>Major</severity>
        <threshold>4</threshold>
        <enabled>false</enabled>
    </anomaly-rule>
    <anomaly-rule>
        <name>LongSentence</name>
        <severity>Critical</severity>
        <threshold>35</threshold>
        <enabled>true</enabled>
    </anomaly-rule>
    <anomaly-rule>
        <name>LongWord</name>
        <severity>Major</severity>
        <threshold>8</threshold>
        <enabled>true</enabled>
    </anomaly-rule>
    <anomaly-rule>
        <name>ModalVerb</name>
        <severity>Minor</severity>
        <enabled>false</enabled>
    </anomaly-rule>
    <anomaly-rule>
        <name>ModalVerbSentence</name>
        <severity>Minor</severity>
        <threshold>2</threshold>
        <enabled>true</enabled>
    </anomaly-rule>
    <anomaly-rule>
        <name>NestedSentence</name>
        <severity>Critical</severity>
        <threshold>6</threshold>
        <enabled>true</enabled>
    </anomaly-rule>
    <anomaly-rule>
        <name>NestedSentenceConjunction</name>
        <severity>Major</severity>
        <threshold>3</threshold>
        <enabled>false</enabled>
    </anomaly-rule>
    <anomaly-rule>
        <name>NestedSentenceDelimiter</name>
        <severity>Major</severity>
        <threshold>3</threshold>
        <enabled>false</enabled>
    </anomaly-rule>
    <anomaly-rule>
        <name>NominalStyle</name>
        <severity>Major</severity>
        <threshold>3</threshold>
        <enabled>true</enabled>
    </anomaly-rule>
    <anomaly-rule>
        <name>PassiveVoice</name>
        <severity>Major</severity>
        <enabled>true</enabled>
    </anomaly-rule>
    <anomaly-rule>
        <name>SentencesStartWithSameWord</name>
        <severity>Minor</severity>
        <threshold>2</threshold>
        <enabled>true</enabled>
    </anomaly-rule>
    <anomaly-rule>
        <name>SubjectiveLanguage</name>
        <severity>Minor</severity>
        <enabled>true</enabled>
    </anomaly-rule>
    <anomaly-rule>
        <name>UnnecessarySyllables</name>
        <severity>Minor</severity>
        <enabled>true</enabled>
    </anomaly-rule>
</anomalies>
```

<div id='id-section4'/>
# Configuration & Quality Gate

The generated report "{filename}-rat-report.html" aggregates the results of the different measurements (formulas, statistics and anomalies). Further, a quality gate indicates whether the text is too trivial or too hard to understand.

First, RAT looks for the configuration at the provided argument (-c or --configurationPath). If this parameter is not provided, e.g. is null, or there is no valid file at the location, RAT will look in the directory path of the file that is currently analysed for a file named "rat-config.xml". If both ways fail to obtain a configuration file, the defaultConfig parameter provided by the executor is considered. In case the default configuration is not a file (e.g., is deleted), the rat internal configuration will be loaded.

Based on the conceptional design of the quality gate depicted by SonarQube:

![rat-quality-sonar](/01_docbase/01_doc-images/sonar-quality-gate.PNG)

We developed a similar quality gate for RAT:

![rat-quality-gate](/01_docbase/01_doc-images/rat-quality-gate.PNG)

The default quality gate configuration:

```xml
<quality-gate>
        <anomalies>
            <anomaly>
                <severity>minor</severity>
                <warning-threshold>50</warning-threshold>
                <error-threshold>9999</error-threshold>
            </anomaly>
            <anomaly>
                <severity>major</severity>
                <warning-threshold>1</warning-threshold>
                <error-threshold>30</error-threshold>
            </anomaly>
            <anomaly>
                <severity>critical</severity>
                <warning-threshold>1</warning-threshold>
                <error-threshold>1</error-threshold>
            </anomaly>
        </anomalies>
        <formulas>
            <formula>
                <name>flesch-reading-ease-amstad</name>
                <easy-warning-threshold>75</easy-warning-threshold>
                <hard-warning-threshold>25</hard-warning-threshold>
                <easy-error-threshold>80</easy-error-threshold>
                <hard-error-threshold>20</hard-error-threshold>
            </formula>
            <formula>
                <name>wiener-sachtextformel</name>
                <easy-warning-threshold>7</easy-warning-threshold>
                <hard-warning-threshold>14</hard-warning-threshold>
                <easy-error-threshold>5</easy-error-threshold>
                <hard-error-threshold>16</hard-error-threshold>
            </formula>
        </formulas>
        <statistics>
            <statistic>
                <name>average-number-of-words-per-sentence</name>
                <easy-warning-threshold>7</easy-warning-threshold>
                <hard-warning-threshold>19</hard-warning-threshold>
                <easy-error-threshold>5</easy-error-threshold>
                <hard-error-threshold>26</hard-error-threshold>
            </statistic>
            <statistic>
                <name>average-number-of-syllables-per-word</name>
                <easy-warning-threshold>1.4</easy-warning-threshold>
                <hard-warning-threshold>3</hard-warning-threshold>
                <easy-error-threshold>1.2</easy-error-threshold>
                <hard-error-threshold>4</hard-error-threshold>
            </statistic>
        </statistics>
    </quality-gate>
```