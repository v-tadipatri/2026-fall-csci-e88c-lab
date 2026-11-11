package org.cscie88c.kafka.model

import org.apache.kafka.streams.kstream.ValueJoiner

class WordPosJoiner extends ValueJoiner[String,String,WordDef]{

  override def apply(word: String, pos: String): WordDef = {
    WordDef(word, pos) 
  }
}
