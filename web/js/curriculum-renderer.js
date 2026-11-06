// ==========================================
// CURRICULUM RENDERER & STRICT GATING ENGINE
// Isolated Lessons, 100% Verbatim Content, Media Tracking,
// Mandatory Written Exercises, and Supabase Persistence
// ==========================================

(function() {
    // 1. Helper: Collect all required input field IDs for a lesson
    function getLessonFieldIds(lesson) {
        if (!lesson) return [];
        const ids = [];

        // Reading questions
        (lesson.readings || []).forEach(r => {
            (r.questions || []).forEach(q => {
                if (q && q.id) ids.push(q.id);
            });
        });

        // Listening tasks
        (lesson.listeningTasks || []).forEach(lt => {
            (lt.items || []).forEach((item, idx) => {
                ids.push(item.id || `${lt.id}_item_${idx}`);
            });
            (lt.followUp || []).forEach((f, idx) => {
                ids.push(f.id || `${lt.id}_fu_${idx}`);
            });
        });

        // Writing tasks
        (lesson.writingTasks || []).forEach((w, wIdx) => {
            if (w.template && Array.isArray(w.template)) {
                w.template.forEach((t, tIdx) => {
                    ids.push(t.id || `${w.id || 'w_' + wIdx}_t_${tIdx}`);
                });
            }
            if (w.items && Array.isArray(w.items)) {
                w.items.forEach((item, iIdx) => {
                    ids.push(item.id || `${w.id || 'w_' + wIdx}_item_${iIdx}`);
                });
            }
            if (w.placeholder || (!w.template && !w.items)) {
                ids.push(w.id || `writing_task_${lesson.number}_${wIdx}`);
            }
        });

        // Exercises / Test Yourself
        (lesson.exercises || []).forEach((ex, exIdx) => {
            if (ex.items && Array.isArray(ex.items)) {
                ex.items.forEach((item, iIdx) => {
                    ids.push(item.id || `${ex.id || 'ex_' + exIdx}_item_${iIdx}`);
                });
            } else if (ex.subsections && Array.isArray(ex.subsections)) {
                ex.subsections.forEach((sub, sIdx) => {
                    if (sub.items && Array.isArray(sub.items)) {
                        sub.items.forEach((si, siIdx) => {
                            ids.push(si.id || `${sub.id || 'sub_' + sIdx}_item_${siIdx}`);
                        });
                    } else if (sub.questions && Array.isArray(sub.questions)) {
                        sub.questions.forEach((sq, sqIdx) => {
                            ids.push(sq.id || `${sub.id || 'sub_' + sIdx}_q_${sqIdx}`);
                        });
                    } else {
                        ids.push(sub.id || `${ex.id}_sub_${sIdx}`);
                    }
                });
            } else {
                ids.push(ex.id || `exercise_${lesson.number}_${exIdx}`);
            }
        });

        return Array.from(new Set(ids));
    }

    // 2. Answers Storage in LocalStorage
    function getLessonAnswers(userId, lessonNum) {
        if (!userId || !lessonNum) return {};
        try {
            const raw = localStorage.getItem(`YSL_ANSWERS_${userId}_L${lessonNum}`);
            return raw ? JSON.parse(raw) : {};
        } catch (e) {
            return {};
        }
    }

    function saveLessonFieldAnswer(lessonNum, fieldId, value) {
        const u = window.currentUser;
        if (!u || !u.id || !lessonNum || !fieldId) return;
        try {
            const answers = getLessonAnswers(u.id, lessonNum);
            answers[fieldId] = String(value || "").trim();
            localStorage.setItem(`YSL_ANSWERS_${u.id}_L${lessonNum}`, JSON.stringify(answers));
            updateLessonStatsUI(lessonNum);
        } catch (e) {
            console.error("Error saving lesson answer:", e);
        }
    }

    // 3. Media Completion Tracking
    function isLessonMediaCompleted(userId, lessonNum) {
        if (!userId || !lessonNum) return false;
        if (window.currentUser && window.currentUser.role === 'admin') return true;
        try {
            return localStorage.getItem(`YSL_MEDIA_DONE_${userId}_L${lessonNum}`) === 'true';
        } catch (e) {
            return false;
        }
    }

    function markLessonMediaCompleted(lessonNum) {
        const u = window.currentUser;
        if (!u || !u.id || !lessonNum) return;
        try {
            localStorage.setItem(`YSL_MEDIA_DONE_${u.id}_L${lessonNum}`, 'true');
            updateLessonStatsUI(lessonNum);
            if (typeof showLmsToast === 'function') {
                showLmsToast(`🎧 تم تسجيل استماع ومشاهدة وسائط الدرس ${lessonNum}`, 'success');
            }
        } catch (e) {
            console.error("Error marking media done:", e);
        }
    }

    // 4. Verification & Statistics calculation
    function getLessonCompletionStats(userId, lesson) {
        if (!lesson) return { total: 0, answered: 0, percent: 0, mediaDone: false, isReady: false };
        const ids = getLessonFieldIds(lesson);
        const answers = getLessonAnswers(userId, lesson.number);

        let answeredCount = 0;
        ids.forEach(id => {
            if (answers[id] && answers[id].length > 0) {
                answeredCount++;
            }
        });

        const total = ids.length;
        const percent = total > 0 ? Math.round((answeredCount / total) * 100) : 100;
        const exercisesDone = total === 0 || answeredCount >= total;
        const mediaDone = isLessonMediaCompleted(userId, lesson.number);
        const isAdmin = window.currentUser && window.currentUser.role === 'admin';

        return {
            total,
            answered: answeredCount,
            percent,
            exercisesDone,
            mediaDone,
            isReady: isAdmin || (exercisesDone && mediaDone)
        };
    }

    function updateLessonStatsUI(lessonNum) {
        if (typeof document === 'undefined') return;
        const u = window.currentUser;
        if (!u || !u.id) return;
        const lessons = typeof getActiveLessons === 'function' ? getActiveLessons() : (window.BEGINNER_LESSONS_DATA || []);
        const lesson = lessons.find(l => l.number === lessonNum);
        if (!lesson) return;

        const stats = getLessonCompletionStats(u.id, lesson);

        // Update Media status badges
        const mediaStatusEl = document.getElementById(`media-status-badge-${lessonNum}`);
        if (mediaStatusEl) {
            if (stats.mediaDone) {
                mediaStatusEl.className = "flex items-center gap-1.5 text-xs font-bold text-emerald-700 bg-emerald-50 px-2.5 py-1 rounded-xl border border-emerald-200";
                mediaStatusEl.innerHTML = `<i data-lucide="check-circle-2" class="w-4 h-4 text-emerald-600"></i><span>مكتمل بنجاح (100%)</span>`;
            } else {
                mediaStatusEl.className = "flex items-center gap-1.5 text-xs font-bold text-amber-700 bg-amber-50 px-2.5 py-1 rounded-xl border border-amber-200";
                mediaStatusEl.innerHTML = `<i data-lucide="clock" class="w-4 h-4 text-amber-600"></i><span>بانتظار الاستماع أو المشاهدة</span>`;
            }
        }

        // Update Exercises status badges
        const exStatusEl = document.getElementById(`exercise-status-badge-${lessonNum}`);
        if (exStatusEl) {
            if (stats.exercisesDone) {
                exStatusEl.className = "flex items-center gap-1.5 text-xs font-bold text-emerald-700 bg-emerald-50 px-2.5 py-1 rounded-xl border border-emerald-200";
                exStatusEl.innerHTML = `<i data-lucide="check-circle-2" class="w-4 h-4 text-emerald-600"></i><span>تم حل كافة التمارين (${stats.answered}/${stats.total} - 100%)</span>`;
            } else {
                exStatusEl.className = "flex items-center gap-1.5 text-xs font-bold text-slate-700 bg-slate-100 px-2.5 py-1 rounded-xl border border-slate-200";
                exStatusEl.innerHTML = `<i data-lucide="edit-3" class="w-4 h-4 text-teal-600"></i><span>تمت إجابة ${stats.answered} من ${stats.total} (${stats.percent}%)</span>`;
            }
        }

        // Update Progress bar
        const pBar = document.getElementById(`exercise-progress-bar-${lessonNum}`);
        if (pBar) {
            pBar.style.width = `${stats.percent}%`;
        }

        // Update Unlock Next button state
        const unlockBtn = document.getElementById(`btn-submit-unlock-${lessonNum}`);
        if (unlockBtn) {
            const isCompleted = u.completedLessons && u.completedLessons.includes(lessonNum);
            if (stats.isReady || isCompleted) {
                unlockBtn.className = "w-full sm:w-auto px-6 py-3 rounded-2xl bg-gradient-to-r from-teal-DEFAULT to-teal-dark hover:from-teal-dark hover:to-teal-DEFAULT text-white font-black text-sm transition flex items-center justify-center gap-2 shadow-lg shadow-teal-500/25 cursor-pointer ring-2 ring-teal-400/40";
                unlockBtn.disabled = false;
                unlockBtn.innerHTML = `<i data-lucide="${isCompleted ? 'check-check' : 'unlock'}" class="w-5 h-5"></i><span>${isCompleted ? '✓ تم إنجاز الدرس واعتماده في Supabase' : 'اعتماد الإجابات وإتمام الدرس وفتح التالي'}</span>`;
            } else {
                unlockBtn.className = "w-full sm:w-auto px-6 py-3 rounded-2xl bg-slate-100 hover:bg-slate-200 text-slate-500 font-bold text-sm transition flex items-center justify-center gap-2 border border-slate-200 cursor-not-allowed";
                unlockBtn.disabled = false; // keep clickable so clicking explains missing requirements
                unlockBtn.innerHTML = `<i data-lucide="lock" class="w-5 h-5 text-slate-400"></i><span>أكمل شروط الدرس لفتح التالي (${stats.percent}%)</span>`;
            }
        }

        if (window.lucide) lucide.createIcons();
    }

    // 5. Submit Lesson and Unlock Next Lesson
    async function submitLessonAndUnlockNext(lessonNum) {
        const u = window.currentUser;
        if (!u) return;

        const lessons = typeof getActiveLessons === 'function' ? getActiveLessons() : (window.BEGINNER_LESSONS_DATA || []);
        const lesson = lessons.find(l => l.number === lessonNum);
        if (!lesson) return;

        const stats = getLessonCompletionStats(u.id, lesson);

        // Strict Gating: Enforce both conditions for non-admin employees
        if (u.role !== 'admin') {
            if (!stats.mediaDone) {
                if (typeof showLmsToast === 'function') {
                    showLmsToast(`⚠️ الشرط الأول غير مكتمل: يجب الاستماع للمقطع الصوتي أو مشاهدة فيديو الشرح أولاً!`, 'error');
                }
                const audioBtn = document.getElementById(`audio-play-btn-${lessonNum}`);
                if (audioBtn) audioBtn.scrollIntoView({ behavior: 'smooth', block: 'center' });
                return;
            }

            if (!stats.exercisesDone) {
                if (typeof showLmsToast === 'function') {
                    showLmsToast(`⚠️ الشرط الثاني غير مكتمل: يجب حل وكتابة إجابات كافة التمارين بنسبة 100% (متبقي ${stats.total - stats.answered} سؤال)!`, 'error');
                }
                const exContainer = document.getElementById(`lesson-exercises-block-${lessonNum}`);
                if (exContainer) exContainer.scrollIntoView({ behavior: 'smooth', block: 'start' });
                return;
            }
        }

        // If conditions are met or user is admin: Mark complete & sync to Supabase
        if (typeof toggleLessonCompletion === 'function') {
            if (!u.completedLessons) u.completedLessons = [];
            if (!u.completedLessons.includes(lessonNum)) {
                await toggleLessonCompletion(lessonNum);
            }
        }

        if (typeof confetti === 'function') {
            confetti({ particleCount: 70, spread: 80, origin: { y: 0.6 } });
        }

        if (typeof showLmsToast === 'function') {
            showLmsToast(`🎉 تهانينا ${u.name}! تم اعتماد الدرس ${lessonNum} وحفظه في Supabase بنجاح!`, 'success');
        }

        // Auto navigate to the newly unlocked next lesson if exists
        const nextNum = lessonNum + 1;
        if (nextNum <= lessons.length && typeof selectLesson === 'function') {
            setTimeout(() => {
                selectLesson(nextNum);
                window.scrollTo({ top: 0, behavior: 'smooth' });
            }, 700);
        }
    }

    // 6. Speech synthesis helper from data-attribute
    function speakFromAttr(el) {
        if (!el) return;
        const text = el.getAttribute('data-speak') || '';
        if (text && typeof speakText === 'function') {
            speakText(text);
        }
    }

    // 7. Full Isolated Lesson View Renderer
    function renderLessonIsolatedView(lesson, currentUser, lessons, levelData) {
        const u = currentUser || { id: 'guest', name: 'المتعلم', completedLessons: [] };
        const savedAnswers = getLessonAnswers(u.id, lesson.number);
        const stats = getLessonCompletionStats(u.id, lesson);
        const isCompleted = u.completedLessons && u.completedLessons.includes(lesson.number);

        return `
            <div class="bg-white p-5 sm:p-8 rounded-3xl border border-slate-200/90 shadow-sm space-y-7">
                <!-- Lesson Header & Top Actions -->
                <div class="border-b border-slate-100 pb-5 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                    <div>
                        <div class="flex items-center gap-2 flex-wrap">
                            <span class="text-xs font-black text-teal-DEFAULT bg-teal-50 px-3 py-1 rounded-full border border-teal-200">الدرس ${lesson.number} من ${lessons.length}</span>
                            <span class="text-xs font-bold bg-navy/5 text-navy px-3 py-1 rounded-full border border-navy/10">${levelData.nameAr}</span>
                            <span class="text-xs font-bold ${isCompleted ? 'bg-emerald-100 text-emerald-800' : 'bg-amber-100 text-amber-800'} px-2.5 py-1 rounded-full flex items-center gap-1">
                                <i data-lucide="${isCompleted ? 'check-circle' : 'clock'}" class="w-3.5 h-3.5"></i>
                                <span>${isCompleted ? 'مكتمل ومسجل في Supabase' : 'قيد التدريب والتسليم'}</span>
                            </span>
                        </div>
                        <h3 class="text-2xl sm:text-3xl font-black text-navy mt-2.5">${lesson.title}</h3>
                        <h4 class="text-sm font-semibold text-slate-500 mt-0.5">${lesson.titleAr || ''}</h4>
                    </div>
                    <div class="flex items-center gap-2 flex-wrap">
                        <button onclick="submitLessonAndUnlockNext(${lesson.number})" id="btn-submit-unlock-${lesson.number}" class="px-4 py-2.5 rounded-xl ${stats.isReady || isCompleted ? 'bg-teal-DEFAULT hover:bg-teal-light text-white ring-2 ring-teal-400/40' : 'bg-slate-100 hover:bg-slate-200 text-slate-500 border border-slate-200'} text-xs font-bold transition flex items-center gap-1.5 shadow-sm">
                            <i data-lucide="${isCompleted ? 'check-circle-2' : stats.isReady ? 'unlock' : 'lock'}" class="w-4 h-4"></i>
                            <span>${isCompleted ? '✓ مكتمل ومعتمد في LMS' : 'اعتماد الإجابات وإتمام الدرس'}</span>
                        </button>
                        <button onclick="jumpToWritingForLesson(${lesson.number})" class="px-4 py-2.5 rounded-xl bg-cyan-700 hover:bg-cyan-800 text-white text-xs font-bold transition flex items-center gap-1.5 shadow-sm">
                            <i data-lucide="edit-3" class="w-4 h-4"></i>
                            <span>ورشة الكتابة</span>
                        </button>
                    </div>
                </div>

                <!-- Strict Gating & Completion Checklist Card -->
                <div class="p-5 sm:p-6 rounded-2xl bg-gradient-to-br from-slate-50 via-teal-50/20 to-slate-50 border border-slate-200/90 space-y-4">
                    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                        <div class="flex items-center gap-3">
                            <div class="w-11 h-11 rounded-2xl bg-navy text-white flex items-center justify-center font-bold text-lg flex-shrink-0 shadow-sm">
                                <i data-lucide="shield-check" class="w-6 h-6 text-teal-300"></i>
                            </div>
                            <div>
                                <h4 class="text-sm font-black text-navy">نظام الإغلاق التسلسلي وشروط فتح الدرس التالي:</h4>
                                <p class="text-xs text-slate-500">يتطلب الانتقال للدرس التالي استيفاء الشرطين الإجباريين أدناه بنسبة 100%</p>
                            </div>
                        </div>
                        <div class="text-left flex-shrink-0">
                            <span class="text-[11px] text-slate-400 block">نسبة حل التمارين:</span>
                            <span class="text-base font-black text-navy">${stats.percent}% (${stats.answered} / ${stats.total})</span>
                        </div>
                    </div>

                    <!-- Progress Bar -->
                    <div class="w-full bg-slate-200 h-2.5 rounded-full overflow-hidden">
                        <div id="exercise-progress-bar-${lesson.number}" class="bg-gradient-to-r from-teal-DEFAULT to-teal-light h-full transition-all duration-300" style="width: ${stats.percent}%"></div>
                    </div>

                    <!-- 2 Mandatory Conditions Checklist Grid -->
                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-1">
                        <!-- Condition 1: Media Completion -->
                        <div class="p-3.5 rounded-xl bg-white border border-slate-200 flex items-center justify-between gap-2 shadow-xs">
                            <div class="flex items-center gap-2.5">
                                <div class="w-8 h-8 rounded-lg bg-purple-100 text-purple-700 flex items-center justify-center flex-shrink-0">
                                    <i data-lucide="headphones" class="w-4 h-4"></i>
                                </div>
                                <div>
                                    <span class="text-xs font-bold text-navy block">1. الاستماع ومشاهدة وسائط الدرس:</span>
                                    <span class="text-[11px] text-slate-500">تشغيل الصوت أو الفيديو المرفق</span>
                                </div>
                            </div>
                            <div id="media-status-badge-${lesson.number}">
                                ${stats.mediaDone ? `
                                    <span class="flex items-center gap-1.5 text-xs font-bold text-emerald-700 bg-emerald-50 px-2.5 py-1 rounded-xl border border-emerald-200">
                                        <i data-lucide="check-circle-2" class="w-4 h-4 text-emerald-600"></i>
                                        <span>مكتمل بنجاح</span>
                                    </span>
                                ` : `
                                    <button onclick="markLessonMediaCompleted(${lesson.number})" class="flex items-center gap-1 text-[11px] font-bold text-purple-700 bg-purple-50 hover:bg-purple-100 px-2.5 py-1 rounded-xl border border-purple-200 transition">
                                        <i data-lucide="check" class="w-3.5 h-3.5"></i>
                                        <span>تأكيد الاستماع</span>
                                    </button>
                                `}
                            </div>
                        </div>

                        <!-- Condition 2: Mandatory Written Exercises -->
                        <div class="p-3.5 rounded-xl bg-white border border-slate-200 flex items-center justify-between gap-2 shadow-xs">
                            <div class="flex items-center gap-2.5">
                                <div class="w-8 h-8 rounded-lg bg-teal-100 text-teal-800 flex items-center justify-center flex-shrink-0">
                                    <i data-lucide="check-square" class="w-4 h-4"></i>
                                </div>
                                <div>
                                    <span class="text-xs font-bold text-navy block">2. حل جميع التمارين والأسئلة:</span>
                                    <span class="text-[11px] text-slate-500">إجابة جميع الحقول بنسبة 100%</span>
                                </div>
                            </div>
                            <div id="exercise-status-badge-${lesson.number}">
                                ${stats.exercisesDone ? `
                                    <span class="flex items-center gap-1.5 text-xs font-bold text-emerald-700 bg-emerald-50 px-2.5 py-1 rounded-xl border border-emerald-200">
                                        <i data-lucide="check-circle-2" class="w-4 h-4 text-emerald-600"></i>
                                        <span>مكتمل (${stats.answered}/${stats.total})</span>
                                    </span>
                                ` : `
                                    <span class="flex items-center gap-1.5 text-xs font-bold text-slate-700 bg-slate-100 px-2.5 py-1 rounded-xl border border-slate-200">
                                        <i data-lucide="edit-3" class="w-4 h-4 text-teal-600"></i>
                                        <span>${stats.answered} من ${stats.total} (${stats.percent}%)</span>
                                    </span>
                                `}
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 1. Learning Objectives (الأهداف والمخرجات التعليمية) -->
                ${renderObjectivesBlock(lesson)}

                <!-- Informal & Formal Greetings (if present in L1) -->
                ${renderGreetingsBlock(lesson)}

                <!-- Useful Vocabulary & Useful Structures (if present) -->
                ${renderUsefulVocabAndStructureBlock(lesson)}

                <!-- 2. Multimedia Hub: Video & Audio Players -->
                ${renderMultimediaHubBlock(lesson)}

                <!-- 3. Readings & Comprehension Questions (نصوص القراءة والفهم) -->
                ${renderReadingsBlock(lesson, savedAnswers)}

                <!-- 4. Dialogues (حوارات المحادثة العملية) -->
                ${renderDialoguesBlock(lesson)}

                <!-- 5. Grammar Mastery (شرح القواعد بالتفصيل) -->
                ${renderGrammarBlock(lesson)}

                <!-- 6. Listening Activities (مهارة الاستماع) -->
                ${renderListeningTasksBlock(lesson, savedAnswers)}

                <!-- 7. Writing Tasks (مهام الكتابة التطبيقية) -->
                ${renderWritingTasksBlock(lesson, savedAnswers)}

                <!-- 8. Test Yourself & Exercises (التمارين والأسئلة الشاملة) -->
                <div id="lesson-exercises-block-${lesson.number}">
                    ${renderExercisesBlock(lesson, savedAnswers)}
                </div>

                <!-- Sequential Lesson Bottom Navigation & Completion Bar -->
                <div class="pt-6 border-t border-slate-200/80 flex flex-col sm:flex-row items-center justify-between gap-4">
                    <div class="w-full sm:w-auto">
                        <button onclick="submitLessonAndUnlockNext(${lesson.number})" class="w-full sm:w-auto px-6 py-3 rounded-2xl text-xs sm:text-sm font-black transition flex items-center justify-center gap-2 shadow-sm ${stats.isReady || isCompleted ? 'bg-gradient-to-r from-teal-DEFAULT to-teal-dark hover:from-teal-dark hover:to-teal-DEFAULT text-white ring-2 ring-teal-400/40 shadow-teal-500/20' : 'bg-slate-100 hover:bg-slate-200 text-slate-600 border border-slate-300'}">
                            <i data-lucide="${isCompleted ? 'check-circle-2' : stats.isReady ? 'unlock' : 'lock'}" class="w-4 h-4 ${isCompleted ? 'text-white' : ''}"></i>
                            <span>${isCompleted ? '✓ تم إنجاز هذا الدرس بنجاح واعتماده' : 'اعتماد الإجابات وإتمام الدرس (فتح التالي)'}</span>
                        </button>
                    </div>

                    <div class="flex items-center gap-2 w-full sm:w-auto justify-end">
                        ${lesson.number > 1 ? `
                            <button onclick="selectLesson(${lesson.number - 1})" class="px-4 py-2.5 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold transition flex items-center gap-1.5">
                                <i data-lucide="arrow-right" class="w-4 h-4"></i>
                                <span>الدرس السابق (L${lesson.number - 1})</span>
                            </button>
                        ` : ''}

                        ${lesson.number < lessons.length ? (
                            (typeof isLessonUnlocked === 'function' && isLessonUnlocked(lesson.number + 1)) ? `
                                <button onclick="selectLesson(${lesson.number + 1})" class="px-5 py-2.5 rounded-xl bg-teal-DEFAULT hover:bg-teal-light text-white text-xs font-bold transition flex items-center gap-1.5 shadow-sm">
                                    <span>الانتقال للدرس التالي (L${lesson.number + 1})</span>
                                    <i data-lucide="arrow-left" class="w-4 h-4"></i>
                                </button>
                            ` : `
                                <button onclick="showLockedLessonAlert(${lesson.number + 1})" class="px-4 py-2.5 rounded-xl bg-slate-100 text-slate-400 border border-slate-200 text-xs font-bold transition flex items-center gap-1.5 cursor-not-allowed hover:bg-slate-200/80" title="يجب إتمام هذا الدرس أولاً لفتح الدرس التالي">
                                    <i data-lucide="lock" class="w-3.5 h-3.5 text-slate-400"></i>
                                    <span>الدرس التالي مقفل (L${lesson.number + 1})</span>
                                </button>
                            `
                        ) : `
                            <button onclick="showCertificateById('${u.id}')" class="px-5 py-2.5 rounded-xl bg-gradient-to-r from-amber-500 to-amber-600 hover:from-amber-600 hover:to-amber-700 text-white text-xs font-bold transition flex items-center gap-1.5 shadow-md shadow-amber-500/20">
                                <i data-lucide="award" class="w-4 h-4"></i>
                                <span>استعراض شهادة التخرج الرسمية</span>
                            </button>
                        `}
                    </div>
                </div>
            </div>
        `;
    }

    // Sub-renderers
    function renderObjectivesBlock(lesson) {
        if (!lesson.objectives || lesson.objectives.length === 0) return '';
        return `
            <div class="p-4 sm:p-5 rounded-2xl bg-teal-50/50 border border-teal-200/80 space-y-3">
                <div class="flex items-center justify-between">
                    <h4 class="text-sm font-extrabold text-navy flex items-center gap-2">
                        <i data-lucide="target" class="w-4 h-4 text-teal-DEFAULT"></i>
                        <span>الأهداف والمخرجات التعليمية للدرس (Learning Objectives):</span>
                    </h4>
                    <span class="text-[11px] font-bold text-teal-800 bg-teal-100/70 px-2 py-0.5 rounded-md">
                        ${lesson.objectives.length} مخرجات أساسية
                    </span>
                </div>
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-2">
                    ${lesson.objectives.map(obj => `
                        <div class="flex items-start gap-2 bg-white/80 p-2.5 rounded-xl border border-teal-100 text-xs text-slate-700">
                            <i data-lucide="check-circle-2" class="w-3.5 h-3.5 text-teal-600 flex-shrink-0 mt-0.5"></i>
                            <span>${obj}</span>
                        </div>
                    `).join('')}
                </div>
                ${lesson.description ? `<p class="text-xs text-slate-600 pt-1 leading-relaxed"><strong class="text-navy">ملخص الوحدة: </strong>${lesson.description}</p>` : ''}
            </div>
        `;
    }

    function renderGreetingsBlock(lesson) {
        if (!lesson.informalGreetings && !lesson.formalGreetings) return '';
        return `
            <div class="space-y-3 pt-1">
                <h4 class="text-base font-bold text-navy flex items-center gap-2">
                    <i data-lucide="smile" class="w-4 h-4 text-amber-500"></i>
                    <span>أنواع التحيات الرسمية وغير الرسمية (Types of Greetings)</span>
                </h4>
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-3.5">
                    <!-- Informal Greetings -->
                    <div class="p-4 rounded-2xl bg-amber-50/50 border border-amber-200 space-y-2">
                        <span class="text-xs font-black text-amber-900 block flex items-center gap-1.5">
                            <i data-lucide="message-circle" class="w-4 h-4 text-amber-600"></i>
                            <span>التحيات غير الرسمية (Informal Greetings) - للأصدقاء والزملاء المقربين:</span>
                        </span>
                        <div class="grid grid-cols-2 gap-2 pt-1">
                            ${(lesson.informalGreetings || []).map(g => `
                                <div class="bg-white p-2.5 rounded-xl border border-amber-200/80 flex items-center justify-between text-xs font-bold text-slate-800 ltr-text" dir="ltr">
                                    <span>${g}</span>
                                    <button type="button" onclick="speakFromAttr(this)" data-speak="${g.replace(/"/g, '&quot;')}" class="p-1 text-amber-600 hover:text-amber-800"><i data-lucide="volume-1" class="w-3.5 h-3.5"></i></button>
                                </div>
                            `).join('')}
                        </div>
                    </div>

                    <!-- Formal Greetings -->
                    <div class="p-4 rounded-2xl bg-blue-50/50 border border-blue-200 space-y-2">
                        <span class="text-xs font-black text-blue-900 block flex items-center gap-1.5">
                            <i data-lucide="briefcase" class="w-4 h-4 text-blue-600"></i>
                            <span>التحيات الرسمية (Formal Greetings) - للمدراء، المقابلات، والعملاء:</span>
                        </span>
                        <div class="grid grid-cols-2 gap-2 pt-1">
                            ${(lesson.formalGreetings || []).map(g => `
                                <div class="bg-white p-2.5 rounded-xl border border-blue-200/80 flex items-center justify-between text-xs font-bold text-slate-800 ltr-text" dir="ltr">
                                    <span>${g}</span>
                                    <button type="button" onclick="speakFromAttr(this)" data-speak="${g.replace(/"/g, '&quot;')}" class="p-1 text-blue-600 hover:text-blue-800"><i data-lucide="volume-1" class="w-3.5 h-3.5"></i></button>
                                </div>
                            `).join('')}
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    function renderUsefulVocabAndStructureBlock(lesson) {
        let html = '';

        // Useful Vocabulary
        if (lesson.usefulVocabulary) {
            html += `
                <div class="space-y-3 pt-2">
                    <h4 class="text-base font-bold text-navy flex items-center gap-2">
                        <i data-lucide="book" class="w-4 h-4 text-emerald-600"></i>
                        <span>المفردات التأسيسية للملزمة (Core Booklet Vocabulary)</span>
                    </h4>
            `;

            if (Array.isArray(lesson.usefulVocabulary)) {
                html += `
                    <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-2.5">
                        ${lesson.usefulVocabulary.map(v => {
                            if (typeof v === 'string') {
                                return `
                                    <div class="p-2.5 rounded-xl bg-white border border-slate-200 text-xs flex items-center justify-between ltr-text" dir="ltr">
                                        <span class="font-bold text-slate-800">${v}</span>
                                        <button type="button" onclick="speakFromAttr(this)" data-speak="${v.replace(/"/g, '&quot;')}" class="p-1 text-emerald-600 hover:text-emerald-800"><i data-lucide="volume-1" class="w-3.5 h-3.5"></i></button>
                                    </div>
                                `;
                            } else {
                                return `
                                    <div class="p-2.5 rounded-xl bg-white border border-slate-200 text-xs space-y-1">
                                        <div class="flex items-center justify-between ltr-text" dir="ltr">
                                            <span class="font-bold text-slate-800">${v.word || v.en || ''}</span>
                                            <button type="button" onclick="speakFromAttr(this)" data-speak="${(v.word || v.en || '').replace(/"/g, '&quot;')}" class="p-1 text-emerald-600 hover:text-emerald-800"><i data-lucide="volume-1" class="w-3.5 h-3.5"></i></button>
                                        </div>
                                        <p class="text-[11px] text-slate-500">${v.meaningAr || v.ar || ''}</p>
                                    </div>
                                `;
                            }
                        }).join('')}
                    </div>
                `;
            } else if (typeof lesson.usefulVocabulary === 'object') {
                html += `<div class="grid grid-cols-1 sm:grid-cols-2 gap-3">`;
                for (let cat in lesson.usefulVocabulary) {
                    const catItems = lesson.usefulVocabulary[cat];
                    html += `
                        <div class="p-4 rounded-2xl bg-slate-50 border border-slate-200 space-y-2">
                            <span class="text-xs font-bold text-navy block">${cat}:</span>
                            <div class="flex flex-wrap gap-1.5">
                                ${(Array.isArray(catItems) ? catItems : []).map(ci => `
                                    <span class="px-2.5 py-1 rounded-lg bg-white border border-slate-200 text-xs font-bold text-slate-700 ltr-text" dir="ltr">${ci}</span>
                                `).join('')}
                            </div>
                        </div>
                    `;
                }
                html += `</div>`;
            }
            html += `</div>`;
        }

        // Useful Expressions
        if (lesson.usefulExpressions) {
            html += `
                <div class="space-y-3 pt-2">
                    <h4 class="text-base font-bold text-navy flex items-center gap-2">
                        <i data-lucide="message-square" class="w-4 h-4 text-teal-600"></i>
                        <span>التعابير والأنماط اللغوية (Useful Expressions)</span>
                    </h4>
                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-2.5">
                        ${(Array.isArray(lesson.usefulExpressions) ? lesson.usefulExpressions : []).map(exp => `
                            <div class="p-3 rounded-xl bg-white border border-slate-200 text-xs flex items-center justify-between ltr-text hover:border-teal-300 transition" dir="ltr">
                                <span class="text-slate-800 font-medium">${exp}</span>
                                <button type="button" onclick="speakFromAttr(this)" data-speak="${exp.replace(/"/g, '&quot;')}" class="p-1 text-teal-600 hover:text-teal-800"><i data-lucide="volume-2" class="w-4 h-4"></i></button>
                            </div>
                        `).join('')}
                    </div>
                </div>
            `;
        }

        // Useful Structure (e.g. telling time rules, imperatives)
        if (lesson.usefulStructure) {
            html += `
                <div class="p-4 sm:p-5 rounded-2xl bg-cyan-50/50 border border-cyan-200 space-y-2.5">
                    <h4 class="text-sm font-black text-navy flex items-center gap-2">
                        <i data-lucide="layers" class="w-4 h-4 text-cyan-700"></i>
                        <span>الهيكل والقواعد البنائية (Useful Structure):</span>
                    </h4>
                    <div class="text-xs sm:text-sm text-slate-700 leading-relaxed space-y-1">
                        ${typeof lesson.usefulStructure === 'string' ? `<p>${lesson.usefulStructure}</p>` : ''}
                        ${Array.isArray(lesson.usefulStructure) ? lesson.usefulStructure.map(s => `<p class="flex items-start gap-1.5"><span class="text-cyan-700 font-black">•</span><span>${s}</span></p>`).join('') : ''}
                    </div>
                </div>
            `;
        }

        return html;
    }

    function renderMultimediaHubBlock(lesson) {
        return `
            <div class="space-y-4 pt-1">
                <div class="flex items-center justify-between">
                    <h4 class="text-base font-bold text-navy flex items-center gap-2">
                        <i data-lucide="play-circle" class="w-5 h-5 text-teal-DEFAULT"></i>
                        <span>المختبر المرئي والصوتي التفاعلي (Multimedia Hub)</span>
                    </h4>
                    <span class="text-xs font-bold text-teal-700 bg-teal-50 border border-teal-200 px-2.5 py-0.5 rounded-full">
                        دقة 1080p + صوت عالي النقاء
                    </span>
                </div>

                ${typeof renderLessonVideoBlock === 'function' ? renderLessonVideoBlock(lesson) : ''}

                <!-- Interactive Audio Player -->
                <div class="p-4 sm:p-5 rounded-2xl bg-slate-50 border border-slate-200/80 space-y-3">
                    <div class="flex items-center justify-between flex-wrap gap-2">
                        <div class="flex items-center gap-2.5">
                            <div class="w-10 h-10 rounded-xl bg-purple-100 text-purple-700 flex items-center justify-center flex-shrink-0">
                                <i data-lucide="headphones" class="w-5 h-5"></i>
                            </div>
                            <div>
                                <h5 class="text-sm font-extrabold text-navy">مشغل الاستماع الصوتي التفاعلي (Audio Listening Player)</h5>
                                <p class="text-[11px] text-slate-500">نطق نقي بلكنة واضحة مع إمكانية إبطاء السرعة للتدريب والترديد</p>
                            </div>
                        </div>
                        <div class="flex items-center gap-1.5 bg-white p-1 rounded-xl border border-slate-200">
                            <span class="text-[10px] text-slate-400 px-1 font-bold">سرعة الصوت:</span>
                            <button onclick="setGlobalAudioSpeed(0.75)" id="aspeed-075" class="px-2 py-0.5 rounded-lg text-xs font-bold text-slate-600 hover:bg-slate-100 transition">0.75x</button>
                            <button onclick="setGlobalAudioSpeed(1.0)" id="aspeed-100" class="px-2 py-0.5 rounded-lg text-xs font-bold bg-navy text-white shadow-sm transition">1.0x</button>
                            <button onclick="setGlobalAudioSpeed(1.25)" id="aspeed-125" class="px-2 py-0.5 rounded-lg text-xs font-bold text-slate-600 hover:bg-slate-100 transition">1.25x</button>
                        </div>
                    </div>

                    <div class="p-3.5 rounded-xl bg-white border border-slate-200 flex items-center gap-3">
                        <button onclick="toggleAudioLessonNarrative(${lesson.number})" id="audio-play-btn-${lesson.number}" class="w-10 h-10 rounded-xl bg-teal-DEFAULT hover:bg-teal-light text-white flex items-center justify-center shadow-sm flex-shrink-0 transition">
                            <i data-lucide="play" id="audio-play-icon-${lesson.number}" class="w-5 h-5 ml-0.5"></i>
                        </button>
                        <div class="flex-1 space-y-1">
                            <div class="flex items-center justify-between text-xs font-bold text-navy">
                                <span id="audio-status-label-${lesson.number}">استمع إلى قراءة نصوص وشرح الدرس</span>
                                <span id="audio-time-label-${lesson.number}" class="ltr-text text-slate-400 font-mono text-[11px]">00:00 / 02:40</span>
                            </div>
                            <div class="w-full bg-slate-100 h-2 rounded-full overflow-hidden cursor-pointer" onclick="seekAudioTrack(${lesson.number}, event)">
                                <div id="audio-progress-bar-${lesson.number}" class="bg-teal-DEFAULT h-full w-0 transition-all duration-300"></div>
                            </div>
                        </div>
                        <button onclick="stopAudioListening()" class="p-2 text-slate-400 hover:text-rose-600 rounded-lg hover:bg-slate-50 transition" title="إيقاف مؤقت">
                            <i data-lucide="square" class="w-4 h-4"></i>
                        </button>
                        <button onclick="markLessonMediaCompleted(${lesson.number})" class="px-3 py-2 rounded-xl bg-purple-50 hover:bg-purple-100 text-purple-700 text-xs font-bold transition flex items-center gap-1 border border-purple-200" title="تأكيد اكتمال الاستماع">
                            <i data-lucide="check" class="w-3.5 h-3.5"></i>
                            <span>تأكيد الاستماع</span>
                        </button>
                    </div>
                </div>
            </div>
        `;
    }

    function renderReadingsBlock(lesson, savedAnswers) {
        if (!lesson.readings || lesson.readings.length === 0) return '';
        return lesson.readings.map((r, rIdx) => `
            <div class="space-y-3 pt-2">
                <div class="flex items-center justify-between flex-wrap gap-2">
                    <h4 class="text-base font-bold text-navy flex items-center gap-2">
                        <i data-lucide="book-open" class="w-4 h-4 text-teal-DEFAULT"></i>
                        <span>${r.title}</span>
                        ${r.titleAr ? `<span class="text-xs font-normal text-slate-500">(${r.titleAr})</span>` : ''}
                    </h4>
                    <button type="button" onclick="speakFromAttr(this)" data-speak="${(r.text || '').replace(/"/g, '&quot;')}" class="px-3 py-1 rounded-lg bg-slate-100 hover:bg-slate-200 text-xs font-bold text-slate-700 flex items-center gap-1.5 transition">
                        <i data-lucide="volume-2" class="w-3.5 h-3.5 text-teal-DEFAULT"></i> استمع للنص كاملاً
                    </button>
                </div>
                <div class="p-5 rounded-2xl bg-white border border-slate-200 text-slate-800 text-sm en-font leading-relaxed whitespace-pre-line ltr-text" dir="ltr">
                    ${r.text}
                </div>
                ${(r.arabicSummary || r.summary) ? `
                    <div class="p-3.5 rounded-xl bg-slate-50 border border-slate-200/80 text-xs text-slate-700 leading-relaxed">
                        <strong class="text-teal-800">💡 خلاصة وترجمة الفكرة الأساسية: </strong>${r.arabicSummary || r.summary}
                    </div>
                ` : ''}

                <!-- Reading Comprehension Questions -->
                ${(r.questions && r.questions.length > 0) ? `
                    <div class="p-4 sm:p-5 rounded-2xl bg-slate-50 border border-slate-200 space-y-3">
                        <span class="text-xs font-extrabold text-navy flex items-center gap-1.5">
                            <i data-lucide="help-circle" class="w-4 h-4 text-teal-600"></i>
                            <span>أسئلة الفهم والاستيعاب القرائي (Comprehension Questions):</span>
                        </span>
                        <div class="space-y-2.5">
                            ${r.questions.map(q => {
                                const val = savedAnswers[q.id] || '';
                                return `
                                    <div class="bg-white p-3 rounded-xl border border-slate-200 space-y-1.5">
                                        <p class="text-xs font-bold text-slate-800 ltr-text" dir="ltr">${q.text}</p>
                                        <input type="text" id="answer_${q.id}" value="${val.replace(/"/g, '&quot;')}" oninput="saveLessonFieldAnswer(${lesson.number}, '${q.id}', this.value)" placeholder="${q.placeholder || 'Write your answer here...'}" class="w-full px-3 py-2 rounded-lg border border-slate-300 text-xs text-navy outline-none focus:border-teal-DEFAULT bg-slate-50 font-mono ltr-text" dir="ltr">
                                    </div>
                                `;
                            }).join('')}
                        </div>
                    </div>
                ` : ''}
            </div>
        `).join('');
    }

    function renderDialoguesBlock(lesson) {
        if (!lesson.dialogues || lesson.dialogues.length === 0) return '';
        return `
            <div class="space-y-3 pt-2">
                <h4 class="text-base font-bold text-navy flex items-center gap-2">
                    <i data-lucide="message-square" class="w-4 h-4 text-purple-600"></i>
                    <span>حوارات المحادثة العملية (Speaking Dialogues)</span>
                </h4>
                <div class="space-y-3">
                    ${lesson.dialogues.map(d => `
                        <div class="p-4 sm:p-5 rounded-2xl bg-slate-50 border border-slate-200 text-xs sm:text-sm en-font space-y-3">
                            <div class="flex items-center justify-between flex-wrap gap-2">
                                <span class="font-bold text-navy">${d.title || 'Conversation'}</span>
                                ${d.context ? `<span class="text-[11px] text-slate-500 bg-white px-2.5 py-0.5 rounded-lg border border-slate-200">سياق المحادثة: ${d.context}</span>` : ''}
                            </div>
                            <div class="space-y-2.5 ltr-text" dir="ltr">
                                <div class="flex items-start gap-2.5 bg-white p-3 rounded-xl border border-slate-200">
                                    <div class="w-7 h-7 rounded-lg bg-teal-100 text-teal-800 font-extrabold flex items-center justify-center text-xs flex-shrink-0">A</div>
                                    <div class="flex-1">
                                        <span class="font-extrabold text-teal-800 block text-xs">${d.speakerA || 'Speaker A'}:</span>
                                        <span class="text-slate-800">${d.textA || ''}</span>
                                        ${d.translationA ? `<p class="text-[11px] text-slate-500 mt-0.5" dir="rtl">${d.translationA}</p>` : ''}
                                    </div>
                                    <button type="button" onclick="speakFromAttr(this)" data-speak="${(d.textA || '').replace(/"/g, '&quot;')}" class="p-1.5 text-slate-400 hover:text-teal-DEFAULT rounded-lg hover:bg-slate-100 transition"><i data-lucide="volume-1" class="w-4 h-4"></i></button>
                                </div>
                                <div class="flex items-start gap-2.5 bg-white p-3 rounded-xl border border-slate-200">
                                    <div class="w-7 h-7 rounded-lg bg-purple-100 text-purple-800 font-extrabold flex items-center justify-center text-xs flex-shrink-0">B</div>
                                    <div class="flex-1">
                                        <span class="font-extrabold text-purple-800 block text-xs">${d.speakerB || 'Speaker B'}:</span>
                                        <span class="text-slate-800">${d.textB || ''}</span>
                                        ${d.translationB ? `<p class="text-[11px] text-slate-500 mt-0.5" dir="rtl">${d.translationB}</p>` : ''}
                                    </div>
                                    <button type="button" onclick="speakFromAttr(this)" data-speak="${(d.textB || '').replace(/"/g, '&quot;')}" class="p-1.5 text-slate-400 hover:text-purple-600 rounded-lg hover:bg-slate-100 transition"><i data-lucide="volume-1" class="w-4 h-4"></i></button>
                                </div>
                            </div>
                        </div>
                    `).join('')}
                </div>
            </div>
        `;
    }

    function renderGrammarBlock(lesson) {
        if (!lesson.grammarDetails || lesson.grammarDetails.length === 0) return '';
        return `
            <div class="space-y-4 pt-2">
                <h4 class="text-base font-bold text-navy flex items-center gap-2">
                    <i data-lucide="compass" class="w-4 h-4 text-amber-600"></i>
                    <span>شرح القواعد بالتفصيل (Detailed Grammar Mastery)</span>
                </h4>
                <div class="space-y-4">
                    ${lesson.grammarDetails.map((g, gIdx) => `
                        <div class="p-5 sm:p-6 rounded-2xl bg-amber-50/40 border border-amber-200/90 space-y-4">
                            <div class="flex items-center justify-between border-b border-amber-200/60 pb-3">
                                <h5 class="text-sm font-black text-navy flex items-center gap-2">
                                    <span class="w-6 h-6 rounded-lg bg-amber-500 text-white text-xs font-bold flex items-center justify-center">${gIdx + 1}</span>
                                    <span>${g.title}</span>
                                    ${g.titleAr ? `<span class="text-xs text-slate-500 font-normal">(${g.titleAr})</span>` : ''}
                                </h5>
                                <span class="text-[10px] font-bold text-amber-800 bg-amber-100 px-2 py-0.5 rounded-full">شرح وتطبيق</span>
                            </div>

                            ${g.explanation ? `<p class="text-xs sm:text-sm text-slate-700 leading-relaxed">${g.explanation}</p>` : ''}

                            ${g.formula ? `
                                <div class="p-3.5 rounded-xl bg-white border border-amber-200 text-xs sm:text-sm font-mono ltr-text text-amber-900 font-bold flex items-center gap-2" dir="ltr">
                                    <i data-lucide="code" class="w-4 h-4 text-amber-600 flex-shrink-0"></i>
                                    <span>${g.formula}</span>
                                </div>
                            ` : ''}

                            ${(g.comparisonTable && g.comparisonTable.length > 0) ? `
                                <div class="overflow-x-auto rounded-xl border border-amber-200 bg-white">
                                    <table class="w-full text-xs text-right">
                                        <thead class="bg-amber-100/60 text-navy font-bold">
                                            <tr>
                                                <th class="p-2.5">الحالة / الاستخدام</th>
                                                <th class="p-2.5 ltr-text" dir="ltr">English Pattern</th>
                                                <th class="p-2.5">المعنى بالعربية</th>
                                            </tr>
                                        </thead>
                                        <tbody class="divide-y divide-amber-100">
                                            ${g.comparisonTable.map(row => `
                                                <tr class="hover:bg-amber-50/40">
                                                    <td class="p-2.5 font-bold text-navy">${row.situation || row.col1 || ''}</td>
                                                    <td class="p-2.5 font-mono text-slate-800 ltr-text" dir="ltr">${row.english || row.col2 || ''}</td>
                                                    <td class="p-2.5 text-slate-600">${row.arabic || row.col3 || ''}</td>
                                                </tr>
                                            `).join('')}
                                        </tbody>
                                    </table>
                                </div>
                            ` : ''}

                            ${(g.examples && g.examples.length > 0) ? `
                                <div class="space-y-2">
                                    <span class="text-xs font-bold text-navy block">أمثلة تطبيقية (Examples):</span>
                                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-2">
                                        ${g.examples.map(ex => `
                                            <div class="p-3 rounded-xl bg-white border border-amber-200/80 text-xs space-y-1">
                                                <div class="flex items-center justify-between ltr-text font-bold text-slate-800" dir="ltr">
                                                    <span>${ex.en}</span>
                                                    <button type="button" onclick="speakFromAttr(this)" data-speak="${(ex.en || '').replace(/"/g, '&quot;')}" class="p-1 text-amber-600 hover:text-amber-800"><i data-lucide="volume-1" class="w-3.5 h-3.5"></i></button>
                                                </div>
                                                <p class="text-slate-600 text-[11px]">${ex.ar}</p>
                                                ${ex.note ? `<p class="text-[10px] text-amber-700 italic font-medium">ملاحظة: ${ex.note}</p>` : ''}
                                            </div>
                                        `).join('')}
                                    </div>
                                </div>
                            ` : ''}

                            ${(g.commonMistakes && g.commonMistakes.length > 0) ? `
                                <div class="p-3.5 rounded-xl bg-white border border-rose-200 space-y-2">
                                    <span class="text-xs font-bold text-rose-700 flex items-center gap-1.5">
                                        <i data-lucide="alert-triangle" class="w-3.5 h-3.5"></i> تجنب هذه الأخطاء الشائعة (Common Mistakes):
                                    </span>
                                    <div class="space-y-2">
                                        ${g.commonMistakes.map(m => `
                                            <div class="text-xs space-y-1 border-b border-slate-100 pb-1.5 last:border-0 last:pb-0">
                                                <div class="flex items-center gap-2 flex-wrap font-mono text-[11px] ltr-text" dir="ltr">
                                                    <span class="text-rose-600 line-through">❌ ${m.incorrect}</span>
                                                    <span class="text-emerald-700 font-bold">✅ ${m.correct}</span>
                                                </div>
                                                ${m.explanation ? `<p class="text-[11px] text-slate-600">${m.explanation}</p>` : ''}
                                            </div>
                                        `).join('')}
                                    </div>
                                </div>
                            ` : ''}
                        </div>
                    `).join('')}
                </div>
            </div>
        `;
    }

    function renderListeningTasksBlock(lesson, savedAnswers) {
        if (!lesson.listeningTasks || lesson.listeningTasks.length === 0) return '';
        return `
            <div class="space-y-4 pt-2">
                <h4 class="text-base font-bold text-navy flex items-center gap-2">
                    <i data-lucide="headphones" class="w-4 h-4 text-purple-700"></i>
                    <span>أنشطة الاستماع الصوتي (Listening Tasks)</span>
                </h4>
                <div class="space-y-4">
                    ${lesson.listeningTasks.map(lt => `
                        <div class="p-5 rounded-2xl bg-purple-50/40 border border-purple-200/90 space-y-3.5">
                            <div class="flex items-center justify-between flex-wrap gap-2">
                                <h5 class="text-xs sm:text-sm font-black text-navy">${lt.title}</h5>
                                <span class="text-[10px] font-bold text-purple-800 bg-purple-100 px-2 py-0.5 rounded-full">Listening Activity</span>
                            </div>
                            <p class="text-xs text-slate-600">${lt.instructions || ''}</p>

                            <!-- Items -->
                            ${(lt.items && lt.items.length > 0) ? `
                                <div class="space-y-2">
                                    ${lt.items.map(item => {
                                        const val = savedAnswers[item.id] || '';
                                        return `
                                            <div class="bg-white p-3 rounded-xl border border-purple-200 space-y-1.5">
                                                <div class="flex items-center justify-between text-xs font-bold text-slate-800 ltr-text" dir="ltr">
                                                    <span>${item.prompt || item.text || ''}</span>
                                                    ${item.spoken ? `<button type="button" onclick="speakFromAttr(this)" data-speak="${(item.spoken || '').replace(/"/g, '&quot;')}" class="p-1 text-purple-600 hover:text-purple-800"><i data-lucide="volume-2" class="w-3.5 h-3.5"></i></button>` : ''}
                                                </div>
                                                <input type="text" id="answer_${item.id}" value="${val.replace(/"/g, '&quot;')}" oninput="saveLessonFieldAnswer(${lesson.number}, '${item.id}', this.value)" placeholder="${item.placeholder || 'Your answer here...'}" class="w-full px-3 py-2 rounded-lg border border-slate-300 text-xs text-navy outline-none focus:border-purple-600 bg-slate-50 font-mono ltr-text" dir="ltr">
                                            </div>
                                        `;
                                    }).join('')}
                                </div>
                            ` : ''}

                            <!-- Follow up questions -->
                            ${(lt.followUp && lt.followUp.length > 0) ? `
                                <div class="space-y-2 pt-2 border-t border-purple-200/60">
                                    <span class="text-xs font-bold text-navy block">Follow-up Questions:</span>
                                    ${lt.followUp.map(fu => {
                                        const val = savedAnswers[fu.id] || '';
                                        return `
                                            <div class="bg-white p-3 rounded-xl border border-purple-200 space-y-1">
                                                <p class="text-xs text-slate-700 font-bold ltr-text" dir="ltr">${fu.text || fu.question || ''}</p>
                                                <input type="text" id="answer_${fu.id}" value="${val.replace(/"/g, '&quot;')}" oninput="saveLessonFieldAnswer(${lesson.number}, '${fu.id}', this.value)" placeholder="${fu.placeholder || 'Answer...'}" class="w-full px-3 py-2 rounded-lg border border-slate-300 text-xs text-navy outline-none focus:border-purple-600 bg-slate-50 font-mono ltr-text" dir="ltr">
                                            </div>
                                        `;
                                    }).join('')}
                                </div>
                            ` : ''}

                            <!-- Transcription toggle -->
                            ${lt.transcription ? `
                                <div class="pt-2">
                                    <button type="button" onclick="const el=document.getElementById('trans_${lt.id}'); el.classList.toggle('hidden');" class="text-xs font-bold text-purple-800 hover:text-purple-950 flex items-center gap-1">
                                        <i data-lucide="file-text" class="w-3.5 h-3.5"></i> إظهار النص الصوتي للتفريغ (Audio Transcription)
                                    </button>
                                    <div id="trans_${lt.id}" class="hidden mt-2 p-3.5 rounded-xl bg-white border border-purple-200 text-xs font-mono text-slate-700 whitespace-pre-line ltr-text" dir="ltr">
                                        ${lt.transcription}
                                    </div>
                                </div>
                            ` : ''}
                        </div>
                    `).join('')}
                </div>
            </div>
        `;
    }

    function renderWritingTasksBlock(lesson, savedAnswers) {
        if (!lesson.writingTasks || lesson.writingTasks.length === 0) return '';
        return `
            <div class="space-y-4 pt-2">
                <h4 class="text-base font-bold text-navy flex items-center gap-2">
                    <i data-lucide="edit-3" class="w-4 h-4 text-cyan-700"></i>
                    <span>مهام الكتابة التطبيقية (Practical Writing Tasks)</span>
                </h4>
                <div class="space-y-4">
                    ${lesson.writingTasks.map(w => {
                        const val = savedAnswers[w.id] || '';
                        return `
                            <div class="p-5 rounded-2xl bg-cyan-50/50 border border-cyan-200/90 space-y-3">
                                <div class="flex items-center justify-between flex-wrap gap-2">
                                    <h5 class="text-xs sm:text-sm font-black text-navy">${w.title || 'Writing Task'}</h5>
                                    <button type="button" onclick="jumpToWritingForLesson(${lesson.number})" class="px-3 py-1 rounded-lg bg-cyan-700 hover:bg-cyan-800 text-white text-xs font-bold flex items-center gap-1">
                                        <i data-lucide="sparkles" class="w-3 h-3 text-cyan-200"></i> ورشة الكتابة
                                    </button>
                                </div>
                                <p class="text-xs sm:text-sm text-slate-700 font-medium ltr-text" dir="ltr">${w.prompt || ''}</p>

                                ${w.template ? `
                                    <div class="space-y-2 bg-white p-3.5 rounded-xl border border-cyan-200">
                                        ${w.template.map(t => {
                                            const tVal = savedAnswers[t.id] || '';
                                            return `
                                                <div class="flex items-center gap-2 text-xs ltr-text" dir="ltr">
                                                    <span class="font-bold text-cyan-900 w-20 flex-shrink-0">${t.speaker}:</span>
                                                    ${t.missing ? `
                                                        <input type="text" id="answer_${t.id}" value="${tVal.replace(/"/g, '&quot;')}" oninput="saveLessonFieldAnswer(${lesson.number}, '${t.id}', this.value)" placeholder="${t.placeholder || 'Complete this line...'}" class="flex-1 px-3 py-1.5 rounded-lg border border-slate-300 text-xs font-mono">
                                                    ` : `
                                                        <span class="text-slate-700">${t.text || ''}</span>
                                                    `}
                                                </div>
                                            `;
                                        }).join('')}
                                    </div>
                                ` : `
                                    <textarea id="answer_${w.id}" oninput="saveLessonFieldAnswer(${lesson.number}, '${w.id}', this.value)" placeholder="${w.placeholder || 'Write your paragraph / sentences here...'}" rows="4" class="w-full p-3.5 rounded-xl border border-slate-300 text-xs text-navy outline-none focus:border-cyan-700 bg-white font-mono ltr-text leading-relaxed" dir="ltr">${val}</textarea>
                                `}

                                ${w.modelAnswer ? `
                                    <div class="pt-1">
                                        <button type="button" onclick="const m=document.getElementById('model_${w.id}'); m.classList.toggle('hidden');" class="text-xs font-bold text-cyan-800 hover:text-cyan-950 flex items-center gap-1">
                                            <i data-lucide="eye" class="w-3.5 h-3.5"></i> عرض نموذج الإجابة المقترح (Model Answer)
                                        </button>
                                        <div id="model_${w.id}" class="hidden mt-2 p-3 rounded-xl bg-white border border-cyan-200 text-xs font-mono text-slate-700 whitespace-pre-line ltr-text" dir="ltr">
                                            ${w.modelAnswer}
                                        </div>
                                    </div>
                                ` : ''}
                            </div>
                        `;
                    }).join('')}
                </div>
            </div>
        `;
    }

    function renderExercisesBlock(lesson, savedAnswers) {
        if (!lesson.exercises || lesson.exercises.length === 0) return '';
        return `
            <div class="space-y-4 pt-3 border-t border-slate-200">
                <div class="flex items-center justify-between">
                    <h4 class="text-base font-black text-navy flex items-center gap-2">
                        <i data-lucide="check-square" class="w-5 h-5 text-teal-DEFAULT"></i>
                        <span>التمارين والأنشطة الشاملة للدرس (Test Yourself Exercises)</span>
                    </h4>
                    <span class="text-xs font-bold text-teal-800 bg-teal-50 border border-teal-200 px-3 py-1 rounded-full">
                        ${lesson.exercises.length} أقسام تقييم
                    </span>
                </div>

                <div class="space-y-4">
                    ${lesson.exercises.map((ex, exIdx) => {
                        return `
                            <div class="p-5 sm:p-6 rounded-2xl bg-slate-50 border border-slate-200/90 space-y-4">
                                <div class="flex items-center justify-between border-b border-slate-200/70 pb-3">
                                    <h5 class="text-xs sm:text-sm font-black text-navy flex items-center gap-2">
                                        <span class="w-6 h-6 rounded-lg bg-navy text-white text-xs font-bold flex items-center justify-center flex-shrink-0">${exIdx + 1}</span>
                                        <span>${ex.title || ex.sectionTitle || 'Exercise'}</span>
                                    </h5>
                                    <span class="text-[10px] font-bold text-slate-500 bg-white px-2 py-0.5 rounded-full border border-slate-200">مطلوب</span>
                                </div>

                                ${renderSingleExerciseBody(lesson, ex, savedAnswers)}
                            </div>
                        `;
                    }).join('')}
                </div>
            </div>
        `;
    }

    function renderSingleExerciseBody(lesson, ex, savedAnswers) {
        // 1. Items with direct fields
        if (ex.items && Array.isArray(ex.items)) {
            return `
                <div class="space-y-2.5">
                    ${ex.items.map(item => {
                        const val = savedAnswers[item.id] || '';
                        if (item.options && Array.isArray(item.options)) {
                            return `
                                <div class="bg-white p-3.5 rounded-xl border border-slate-200 space-y-2">
                                    <p class="text-xs font-bold text-navy ltr-text" dir="ltr">${item.question || item.situation || item.text || item.prompt || ''}</p>
                                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-2">
                                        ${item.options.map(opt => {
                                            const isChecked = val === opt;
                                            return `
                                                <button type="button" onclick="saveLessonFieldAnswer(${lesson.number}, '${item.id}', '${opt.replace(/'/g, "\\'")}'); this.parentElement.querySelectorAll('button').forEach(b => b.className='p-2.5 rounded-xl border border-slate-200 bg-white text-slate-700 text-xs text-left ltr-text hover:border-teal-500 transition'); this.className='p-2.5 rounded-xl border-2 border-teal-DEFAULT bg-teal-50 text-teal-900 font-bold text-xs text-left ltr-text transition';" class="p-2.5 rounded-xl border ${isChecked ? 'border-2 border-teal-DEFAULT bg-teal-50 text-teal-900 font-bold' : 'border-slate-200 bg-white text-slate-700'} text-xs text-left ltr-text hover:border-teal-500 transition" dir="ltr">
                                                    ${opt}
                                                </button>
                                            `;
                                        }).join('')}
                                    </div>
                                </div>
                            `;
                        } else {
                            return `
                                <div class="bg-white p-3.5 rounded-xl border border-slate-200 flex flex-col sm:flex-row sm:items-center justify-between gap-2.5">
                                    <div class="flex-1 text-xs font-bold text-slate-800 ltr-text" dir="ltr">
                                        <span>${item.text || item.prompt || item.situation || item.question || ''}</span>
                                        ${item.suffix ? `<span class="text-slate-500 font-normal ml-1">${item.suffix}</span>` : ''}
                                    </div>
                                    <input type="text" id="answer_${item.id}" value="${val.replace(/"/g, '&quot;')}" oninput="saveLessonFieldAnswer(${lesson.number}, '${item.id}', this.value)" placeholder="${item.placeholder || 'Answer...'}" class="w-full sm:w-56 px-3 py-2 rounded-xl border border-slate-300 text-xs text-navy outline-none focus:border-teal-DEFAULT bg-slate-50 font-mono ltr-text" dir="ltr">
                                </div>
                            `;
                        }
                    }).join('')}
                </div>
            `;
        }

        // 2. Subsections (e.g. Placement Test in Lesson 12)
        if (ex.subsections && Array.isArray(ex.subsections)) {
            return `
                <div class="space-y-4">
                    ${ex.subsections.map(sub => `
                        <div class="p-4 rounded-xl bg-white border border-slate-200 space-y-3">
                            <h6 class="text-xs font-black text-navy">${sub.title || 'Subsection'}</h6>
                            ${sub.items ? `
                                <div class="space-y-2">
                                    ${sub.items.map(si => {
                                        const val = savedAnswers[si.id] || '';
                                        if (si.options) {
                                            return `
                                                <div class="p-3 rounded-lg bg-slate-50 border border-slate-200 space-y-2">
                                                    <p class="text-xs font-bold text-slate-800 ltr-text" dir="ltr">${si.question || si.text || ''}</p>
                                                    <div class="grid grid-cols-1 sm:grid-cols-2 gap-1.5">
                                                        ${si.options.map(opt => {
                                                            const isChecked = val === opt;
                                                            return `
                                                                <button type="button" onclick="saveLessonFieldAnswer(${lesson.number}, '${si.id}', '${opt.replace(/'/g, "\\'")}'); this.parentElement.querySelectorAll('button').forEach(b => b.className='p-2 rounded-lg border border-slate-200 bg-white text-slate-700 text-xs text-left ltr-text'); this.className='p-2 rounded-lg border-2 border-teal-DEFAULT bg-teal-50 text-teal-900 font-bold text-xs text-left ltr-text';" class="p-2 rounded-lg border ${isChecked ? 'border-2 border-teal-DEFAULT bg-teal-50 text-teal-900 font-bold' : 'border-slate-200 bg-white text-slate-700'} text-xs text-left ltr-text" dir="ltr">
                                                                    ${opt}
                                                                </button>
                                                            `;
                                                        }).join('')}
                                                    </div>
                                                </div>
                                            `;
                                        } else {
                                            return `
                                                <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2 p-2.5 rounded-lg bg-slate-50 border border-slate-200">
                                                    <span class="text-xs font-bold text-slate-800 ltr-text" dir="ltr">${si.text || si.prompt || si.question || ''}</span>
                                                    <input type="text" id="answer_${si.id}" value="${val.replace(/"/g, '&quot;')}" oninput="saveLessonFieldAnswer(${lesson.number}, '${si.id}', this.value)" placeholder="${si.placeholder || 'Answer...'}" class="w-full sm:w-52 px-3 py-1.5 rounded-lg border border-slate-300 text-xs bg-white font-mono ltr-text" dir="ltr">
                                                </div>
                                            `;
                                        }
                                    }).join('')}
                                </div>
                            ` : ''}
                        </div>
                    `).join('')}
                </div>
            `;
        }

        // 3. Fallback: multi-line writing or single prompt
        const val = savedAnswers[ex.id] || '';
        return `
            <div class="space-y-2">
                ${ex.prompt ? `<p class="text-xs font-medium text-slate-700 whitespace-pre-line ltr-text" dir="ltr">${ex.prompt}</p>` : ''}
                <textarea id="answer_${ex.id}" oninput="saveLessonFieldAnswer(${lesson.number}, '${ex.id}', this.value)" placeholder="${ex.placeholder || 'Write your complete answer here...'}" rows="5" class="w-full p-3.5 rounded-xl border border-slate-300 text-xs text-navy outline-none focus:border-teal-DEFAULT bg-white font-mono ltr-text leading-relaxed" dir="ltr">${val}</textarea>
            </div>
        `;
    }

    // Export renderer and helpers to window
    window.getLessonFieldIds = getLessonFieldIds;
    window.getLessonAnswers = getLessonAnswers;
    window.saveLessonFieldAnswer = saveLessonFieldAnswer;
    window.isLessonMediaCompleted = isLessonMediaCompleted;
    window.markLessonMediaCompleted = markLessonMediaCompleted;
    window.getLessonCompletionStats = getLessonCompletionStats;
    window.updateLessonStatsUI = updateLessonStatsUI;
    window.submitLessonAndUnlockNext = submitLessonAndUnlockNext;
    window.speakFromAttr = speakFromAttr;
    window.renderLessonIsolatedView = renderLessonIsolatedView;

})();
