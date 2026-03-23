export const DEFAULT_ACTIVITY_LEVEL_RULES = [
  { level: 1, minScore: 0, name: '新手' },
  { level: 2, minScore: 100, name: '作者' },
  { level: 3, minScore: 300, name: '进阶作者' },
  { level: 4, minScore: 600, name: '资深作者' },
  { level: 5, minScore: 1000, name: '传奇作者' }
]

export const DEFAULT_ACTIVITY_LEVEL_RULES_TEXT = JSON.stringify(DEFAULT_ACTIVITY_LEVEL_RULES, null, 2)

export const DEFAULT_ACTIVITY_SCORE_RULES = [
  { type: 'article', name: '发布文章', score: 10 },
  { type: 'comment', name: '发表评论', score: 2 },
  { type: 'favorite', name: '收藏文章', score: 1 },
  { type: 'like', name: '点赞评论', score: 1 },
  { type: 'login', name: '每日登录', score: 1 }
]

export const DEFAULT_ACTIVITY_SCORE_RULES_TEXT = JSON.stringify(DEFAULT_ACTIVITY_SCORE_RULES, null, 2)

function normalizeRule(rule, fallbackLevel) {
  if (!rule || typeof rule !== 'object') {
    return null
  }

  const minScore = Number(rule.minScore)
  if (!Number.isFinite(minScore)) {
    return null
  }

  const level = Number(rule.level)
  return {
    level: Number.isFinite(level) && level > 0 ? Math.floor(level) : fallbackLevel,
    minScore,
    name: typeof rule.name === 'string' && rule.name.trim()
      ? rule.name.trim()
      : '作者'
  }
}

export function parseActivityLevelRules(rawRules) {
  try {
    const source = typeof rawRules === 'string' && rawRules.trim()
      ? JSON.parse(rawRules)
      : rawRules

    if (!Array.isArray(source) || source.length === 0) {
      return DEFAULT_ACTIVITY_LEVEL_RULES
    }

    const rules = source
      .map((rule, index) => normalizeRule(rule, index + 1))
      .filter(Boolean)
      .sort((a, b) => a.minScore - b.minScore)

    return rules.length > 0 ? rules : DEFAULT_ACTIVITY_LEVEL_RULES
  } catch (error) {
    return DEFAULT_ACTIVITY_LEVEL_RULES
  }
}

export function getActivityLevelLabel(score, rawRules) {
  const totalScore = Number(score)
  const safeScore = Number.isFinite(totalScore) ? totalScore : 0
  const rules = parseActivityLevelRules(rawRules)

  let matchedRule = rules[0]
  for (const rule of rules) {
    if (safeScore >= rule.minScore) {
      matchedRule = rule
    }
  }

  if (!matchedRule) {
    return 'Lv.1 新手'
  }

  return `Lv.${matchedRule.level} ${matchedRule.name}`
}

function normalizeScoreRule(rule) {
  if (!rule || typeof rule !== 'object') {
    return null
  }

  const type = typeof rule.type === 'string' ? rule.type.trim() : ''
  const score = Number(rule.score)
  if (!type || !Number.isFinite(score)) {
    return null
  }

  return {
    type,
    name: typeof rule.name === 'string' && rule.name.trim() ? rule.name.trim() : type,
    score: Math.max(0, Math.floor(score))
  }
}

export function parseActivityScoreRules(rawRules) {
  try {
    const source = typeof rawRules === 'string' && rawRules.trim()
      ? JSON.parse(rawRules)
      : rawRules

    if (!Array.isArray(source) || source.length === 0) {
      return DEFAULT_ACTIVITY_SCORE_RULES
    }

    const rules = source
      .map(normalizeScoreRule)
      .filter(Boolean)

    return rules.length > 0 ? rules : DEFAULT_ACTIVITY_SCORE_RULES
  } catch (error) {
    return DEFAULT_ACTIVITY_SCORE_RULES
  }
}
